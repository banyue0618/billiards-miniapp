package org.dromara.billiards.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import org.dromara.billiards.common.constant.BilliardsConstants;
import org.dromara.billiards.convert.PriceRuleConvert;
import org.dromara.billiards.convert.TableConvert;
import org.dromara.billiards.mapper.TableMapper;
import org.dromara.billiards.domain.bo.TableDto;
import org.dromara.billiards.domain.bo.BatchTableRequest;
import org.dromara.billiards.domain.bo.TableQueryRequest;
import org.dromara.billiards.domain.entity.PriceRule;
import org.dromara.billiards.domain.entity.Table;
import org.dromara.billiards.domain.vo.TableVO;
import org.dromara.billiards.service.PriceRuleService;
import org.dromara.billiards.service.TableService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dromara.billiards.common.exception.BilliardsException;
import org.dromara.billiards.common.result.ResultCode;
import org.dromara.common.file.enums.ResourceType;
import org.dromara.system.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.ArrayList;

/**
 * 桌台服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
@DS(BilliardsConstants.DS_BILLIARDS_PLATFORM)
public class TableServiceImpl extends ServiceImpl<TableMapper, Table> implements TableService {

    private final TableConvert tableConvert; // 注入转换器

    private final PriceRuleConvert priceRuleConvert;

    @Autowired
    private FileService fileService;

    @Autowired
    private PriceRuleService priceRuleService;

    private static final String QRCODE_PREFIX = "table_";

    /**
     * 重写 getById 方法，以便在未找到对象时抛出标准异常
     */
    @Override
    public Table getById(Serializable id) {
        Table table = super.getById(id);
        if (table == null) {
            throw BilliardsException.of(ResultCode.TABLE_NOT_EXIST);
        }
        return table;
    }

    @Override
    public List<Table> getTablesByStore(String storeId) {
        if (StringUtils.isEmpty(storeId)) {
             throw BilliardsException.of(ResultCode.PARAM_ERROR, "门店ID不能为空");
        }

        LambdaQueryWrapper<Table> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(Table::getStoreId, storeId);
        queryWrapper.orderByAsc(Table::getTableNumber);

        return this.list(queryWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Table createTable(TableDto tableDto) {
        tableDto.setTableNumber(tableDto.getTablePrefix() + tableDto.getTableNumeric());
        Table table = tableConvert.toEntity(tableDto);
        boolean success = this.save(table);
        if (!success) {
            throw BilliardsException.of(ResultCode.ERROR);
        }

        // 生成二维码
        return generateQrcode(table.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateTable(Table table) {
        if (table == null || StringUtils.isEmpty(table.getId())) {
            throw BilliardsException.of(ResultCode.PARAM_ERROR, "待更新的桌台信息或ID不能为空");
        }
        // 确保桌台存在
        this.getById(table.getId()); // 会在不存在时抛出 TABLE_NOT_EXIST

        boolean success = this.updateById(table);
        if (!success) {
            // 此处情况理论上较少发生，因为 getById 已确认存在
            throw BilliardsException.of(ResultCode.DATABASE_ERROR, "更新桌台失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteTable(String tableId) {
        if (StringUtils.isEmpty(tableId)) {
            throw BilliardsException.of(ResultCode.PARAM_ERROR, "桌台ID不能为空");
        }
        // 先确认桌台是否存在，如果不存在，则认为删除成功 (幂等性)
        Table existingTable = super.getById(tableId); // 使用 super.getById 避免重复抛 TABLE_NOT_EXIST
        if (existingTable == null) {
            return; // 桌台本就不存在，无需操作
        }

        // 删除桌台前，先删除关联的二维码图片
        if (existingTable.getQrcodeUrl() != null && !existingTable.getQrcodeUrl().isEmpty()) {
            try {
                fileService.deleteResource(existingTable.getQrcodeUrl(), ResourceType.QRCODE);
            } catch (Exception e) {
                log.warn("删除桌台二维码图片失败，ID: {}, 图片资源ID: {}, 错误: {}",
                    tableId, existingTable.getQrcodeUrl(), e.getMessage());
                // 继续删除桌台，不因图片删除失败而中断操作
            }
        }

        boolean success = this.removeById(tableId);
        if (!success) {
            throw BilliardsException.of(ResultCode.DATABASE_ERROR, "删除桌台失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchDeleteTables(List<String> ids) {
        if (ids == null || ids.isEmpty()) {
            throw BilliardsException.of(ResultCode.PARAM_ERROR, "桌台ID列表不能为空");
        }

        // 过滤出存在的桌台，并记录所有需要删除的二维码资源ID
        List<String> existingIds = new ArrayList<>();
        for (String id : ids) {
            Table existingTable = super.getById(id);
            if (existingTable != null) {
                existingIds.add(id);

                // 删除关联的二维码图片
                if (existingTable.getQrcodeUrl() != null && !existingTable.getQrcodeUrl().isEmpty()) {
                    try {
                        fileService.deleteResource(existingTable.getQrcodeUrl(), ResourceType.QRCODE);
                    } catch (Exception e) {
                        log.warn("批量删除中，删除桌台二维码图片失败，ID: {}, 图片资源ID: {}, 错误: {}",
                            id, existingTable.getQrcodeUrl(), e.getMessage());
                        // 继续操作，不因单个图片删除失败而中断批量删除
                    }
                }
            }
        }

        if (!existingIds.isEmpty()) {
            boolean success = this.removeByIds(existingIds);
            if (!success) {
                throw BilliardsException.of(ResultCode.DATABASE_ERROR, "批量删除桌台失败");
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(String tableId, Integer status) {
        if (StringUtils.isEmpty(tableId)) {
            throw BilliardsException.of(ResultCode.PARAM_ERROR, "桌台ID不能为空");
        }
        if (status == null || status < 0 || status > 3) { // 校验状态值
            throw BilliardsException.of(ResultCode.PARAM_ERROR, "状态值不合法，必须在0-3之间");
        }

        Table table = this.getById(tableId); // 内部会检查是否存在并抛异常

        // 如果状态未改变，可以提前返回避免数据库操作
        if (Objects.equals(table.getStatus(), status)) {
            log.info("桌台 {} 状态已经是 {}，无需更新。", tableId, status);
            return;
        }

        table.setStatus(status);
        boolean success = this.updateById(table);
        if (!success) {
            throw BilliardsException.of(ResultCode.DATABASE_ERROR, "更新桌台状态失败");
        }
    }

    @Override
    public IPage<Table> getTablePage(TableQueryRequest tableQueryRequest) {
        LambdaQueryWrapper<Table> queryWrapper = Wrappers.lambdaQuery();
        // 如果 storeId 为空，当前行为是查询所有桌台（如果 storeId 为空）(后续根据业务决定是否应该抛异常或返回空分页？)
        queryWrapper.eq(StringUtils.isNotEmpty(tableQueryRequest.getStoreId()), Table::getStoreId, tableQueryRequest.getStoreId());
        queryWrapper.eq(tableQueryRequest.getStatus() != null, Table::getStatus, tableQueryRequest.getStatus());
        queryWrapper.like(StringUtils.isNotEmpty(tableQueryRequest.getTableNumber()), Table::getTableNumber, tableQueryRequest.getTableNumber());
        queryWrapper.like(StringUtils.isNotEmpty(tableQueryRequest.getTableType()), Table::getTableType, tableQueryRequest.getTableType());
        // 使用新字段排序：先按前缀排序，再按数字排序
        queryWrapper.orderByAsc(Table::getTablePrefix)
            .orderByAsc(Table::getTableNumeric);

        Page<Table> tablePage = new Page<>(tableQueryRequest.getPageNumber(), tableQueryRequest.getPageSize());

        return this.page(tablePage, queryWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchCreateTables(BatchTableRequest request) {
        if (request == null) {
            throw BilliardsException.of(ResultCode.PARAM_ERROR, "批量创建请求不能为空");
        }

        // 检查门店是否存在 (可以调用其他服务进行验证)
        String storeId = request.getStoreId();
        if (StringUtils.isEmpty(storeId)) {
            throw BilliardsException.of(ResultCode.PARAM_ERROR, "门店ID不能为空");
        }

        // 获取前缀
        String tablePrefix = request.getTablePrefix() == null ? "" : request.getTablePrefix().trim();

        // 检查编号是否已存在
        List<Table> existingTables = this.listTablesByStore(storeId);
        // 提取所有已存在的前缀和数字组合
        Set<String> existingCombinations = new HashSet<>();
        for (Table table : existingTables) {
            if (table.getTablePrefix() != null && table.getTableNumeric() != null) {
                existingCombinations.add(table.getTablePrefix() + table.getTableNumeric());
            }
        }

        List<Table> tablesToCreate = new java.util.ArrayList<>();
        int startNumeric = request.getStartNumeric();
        int count = request.getCount();

        for (int i = 0; i < count; i++) {
            int numeric = startNumeric + i;
            String combinedNumber = tablePrefix + numeric;

            // 检查组合是否已存在
            if (existingCombinations.contains(combinedNumber)) {
                throw BilliardsException.of(ResultCode.PARAM_ERROR, "桌台编号 " + combinedNumber + " 已存在");
            }

            Table table = new Table();
            table.setStoreId(storeId);
            table.setTablePrefix(tablePrefix);
            table.setTableNumeric(numeric);
            table.setTableNumber(combinedNumber); // 保持向后兼容
            table.setTableType(request.getTableType());
            table.setPriceRuleId(request.getPriceRuleId());
            table.setStatus(0);  // 默认状态为空闲

            tablesToCreate.add(table);
        }

        // 批量保存
        boolean success = this.saveBatch(tablesToCreate);
        if (!success) {
            throw BilliardsException.of(ResultCode.DATABASE_ERROR, "批量创建桌台失败");
        }

        // 为每个桌台生成二维码
        for (Table table : tablesToCreate) {
            generateQrcode(table.getId());
        }
    }

    @Override
    public Table getTableByQrcode(String qrcodeUrl) {
        if (StringUtils.isEmpty(qrcodeUrl)) {
            throw BilliardsException.of(ResultCode.PARAM_ERROR, "二维码URL不能为空");
        }

        LambdaQueryWrapper<Table> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(Table::getQrcodeUrl, qrcodeUrl);

        Table table = this.getOne(queryWrapper);
        if (table == null) {
            throw BilliardsException.of(ResultCode.TABLE_NOT_EXIST, "通过二维码未找到对应桌台");
        }
        return table;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Table generateQrcode(String tableId) {
        if (StringUtils.isEmpty(tableId)) {
            throw BilliardsException.of(ResultCode.PARAM_ERROR, "桌台ID不能为空");
        }

        Table table = this.getById(tableId); // getById 内部会检查是否存在并抛异常

        try {
            // 1. 生成二维码内容 - 可以考虑加入时间戳和签名等安全措施
            String qrCodeContent = "table_" + table.getId();

            // 2. 使用ZXing生成二维码图片
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(qrCodeContent, BarcodeFormat.QR_CODE, 300, 300);
            BufferedImage qrImage = MatrixToImageWriter.toBufferedImage(bitMatrix);

            // 3. 将BufferedImage转换为字节数组
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(qrImage, "png", baos);
            byte[] imageBytes = baos.toByteArray();

            // 4. 上传二维码图片并获取资源ID
            String fileName = "qrcode_" + table.getTableNumber() + ".png";
            Map<String, String> uploadResult = fileService.uploadBytes(imageBytes, fileName, ResourceType.QRCODE);
            String resourceId = uploadResult.get("resourceId");

            // 5. 更新桌台信息，存储resourceId
            table.setQrcodeUrl(resourceId);
            boolean success = this.updateById(table);
            if(!success){
                throw BilliardsException.of(ResultCode.DATABASE_ERROR, "更新二维码信息失败");
            }

            log.info("为桌台 {} 生成/更新了二维码资源ID: {}", tableId, resourceId);
            return table;
        } catch (WriterException | IOException e) {
            log.error("生成二维码失败: {}", e.getMessage(), e);
            throw BilliardsException.of(ResultCode.ERROR, "生成二维码失败");
        }
    }

    @Override
    public TableVO getTableInfo(String id) {
        // 获取桌台信息、价格信息
        Table table = this.getById(id);
        if (table == null) {
            throw BilliardsException.of(ResultCode.TABLE_NOT_EXIST, "桌台不存在");
        }
        TableVO tableVO = tableConvert.toVo(table);// 转换桌台为VO

        PriceRule priceRule = priceRuleService.getById(table.getPriceRuleId());

        tableVO.setPriceRule(priceRuleConvert.toVo(priceRule)); // 设置价格规则VO
        return tableVO;
    }

    @Override
    public Table getTableInfoByQrcode(String qrcodeUrl) {
        if (StringUtils.isEmpty(qrcodeUrl)) {
            throw BilliardsException.of(ResultCode.PARAM_ERROR, "二维码URL不能为空");
        }
        // getTableByQrcode 内部已添加异常处理
        return this.getTableByQrcode(qrcodeUrl);
    }

    @Override
    public List<Table> listTablesByStore(String storeId) {
        LambdaQueryWrapper<Table> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(Table::getStoreId, storeId);
        // 使用新字段排序：先按前缀排序，再按数字排序
        queryWrapper.orderByAsc(Table::getTablePrefix)
                   .orderByAsc(Table::getTableNumeric);

        return this.list(queryWrapper);
    }
}
