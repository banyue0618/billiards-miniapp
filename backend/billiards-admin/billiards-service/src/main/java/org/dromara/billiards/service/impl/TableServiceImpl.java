package org.dromara.billiards.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import org.dromara.billiards.common.constant.BilliardsConstants;
import org.dromara.billiards.common.constant.TableStatusEnum;
import org.dromara.billiards.convert.PriceRuleConvert;
import org.dromara.billiards.convert.TableConvert;
import org.dromara.billiards.domain.entity.BlsTable;
import org.dromara.billiards.mapper.TableMapper;
import org.dromara.billiards.domain.bo.TableDto;
import org.dromara.billiards.domain.bo.BatchTableRequest;
import org.dromara.billiards.domain.bo.TableQueryRequest;
import org.dromara.billiards.domain.entity.BlsPriceRule;
import org.dromara.billiards.domain.vo.TableVO;
import org.dromara.billiards.service.PriceRuleService;
import org.dromara.billiards.service.TableService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
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
import org.dromara.billiards.service.QrCodeTokenService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.dromara.billiards.security.MerchantQueryHelper;
import org.dromara.billiards.security.MerchantWriteGuard;

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
import org.dromara.billiards.domain.vo.ReservationTableVO;
import org.dromara.billiards.domain.vo.TableTagVO;
import org.dromara.billiards.domain.vo.TimeSlotVO;
import org.dromara.billiards.service.IBlsReservationService;
import org.dromara.billiards.service.ReservationConfigService;
import org.dromara.billiards.domain.entity.BlsReservation;
import org.dromara.billiards.common.constant.ReservationStatusEnum;
import org.dromara.billiards.config.BlsReserveConfig;
import org.dromara.common.core.utils.DateUtils;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.stream.Collectors;

/**
 * 桌台服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
@DS(BilliardsConstants.DS_BILLIARDS_PLATFORM)
public class TableServiceImpl extends ServiceImpl<TableMapper, BlsTable> implements TableService {

    private final TableConvert tableConvert; // 注入转换器

    private final PriceRuleConvert priceRuleConvert;

    private final FileService fileService;

    private final PriceRuleService priceRuleService;

    private final QrCodeTokenService qrCodeTokenService;
    private final IBlsReservationService reservationService;
    private final ReservationConfigService reservationConfigService;

    /**
     * 重写 getById 方法，以便在未找到对象时抛出标准异常
     */
    @Override
    public BlsTable getById(Serializable id) {
        BlsTable blsTable = super.getById(id);
        if (blsTable == null) {
            throw BilliardsException.of(ResultCode.TABLE_NOT_EXIST);
        }
        return blsTable;
    }

    @Override
    public List<BlsTable> getTablesByStore(String storeId) {
        if (StringUtils.isEmpty(storeId)) {
             throw BilliardsException.of(ResultCode.PARAM_ERROR, "门店ID不能为空");
        }

        LambdaQueryWrapper<BlsTable> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(BlsTable::getStoreId, storeId);
        MerchantQueryHelper.apply(queryWrapper, BlsTable::getMerchantId);
        queryWrapper.orderByAsc(BlsTable::getTableNumber);

        return this.list(queryWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BlsTable createTable(TableDto tableDto) {
        tableDto.setTableNumber(tableDto.getTablePrefix() + tableDto.getTableNumeric());
        BlsTable blsTable = tableConvert.toEntity(tableDto);
        boolean success = this.save(blsTable);
        if (!success) {
            throw BilliardsException.of(ResultCode.ERROR);
        }

        // 生成二维码
        return generateQrcode(blsTable.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateTable(BlsTable blsTable) {
        if (blsTable == null || StringUtils.isEmpty(blsTable.getId())) {
            throw BilliardsException.of(ResultCode.PARAM_ERROR, "待更新的桌台信息或ID不能为空");
        }
        // 确保桌台存在
        this.getById(blsTable.getId()); // 会在不存在时抛出 TABLE_NOT_EXIST

        // 商户越权
        MerchantWriteGuard.assertWritable(blsTable.getMerchantId());
        boolean success = this.updateById(blsTable);
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
        BlsTable existingBlsTable = super.getById(tableId); // 使用 super.getById 避免重复抛 TABLE_NOT_EXIST
        if (existingBlsTable == null) {
            return; // 桌台本就不存在，无需操作
        }

        // 删除桌台前，先删除关联的二维码图片
        if (existingBlsTable.getQrcodeUrl() != null && !existingBlsTable.getQrcodeUrl().isEmpty()) {
            try {
                fileService.deleteResource(existingBlsTable.getQrcodeUrl(), ResourceType.QRCODE);
            } catch (Exception e) {
                log.warn("删除桌台二维码图片失败，ID: {}, 图片资源ID: {}, 错误: {}",
                    tableId, existingBlsTable.getQrcodeUrl(), e.getMessage());
                // 继续删除桌台，不因图片删除失败而中断操作
            }
        }

        // 商户越权
        MerchantWriteGuard.assertWritable(existingBlsTable.getMerchantId());
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
            BlsTable existingBlsTable = super.getById(id);
            if (existingBlsTable != null) {
                existingIds.add(id);
                MerchantWriteGuard.assertWritable(existingBlsTable.getMerchantId());

                // 删除关联的二维码图片
                if (existingBlsTable.getQrcodeUrl() != null && !existingBlsTable.getQrcodeUrl().isEmpty()) {
                    try {
                        fileService.deleteResource(existingBlsTable.getQrcodeUrl(), ResourceType.QRCODE);
                    } catch (Exception e) {
                        log.warn("批量删除中，删除桌台二维码图片失败，ID: {}, 图片资源ID: {}, 错误: {}",
                            id, existingBlsTable.getQrcodeUrl(), e.getMessage());
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

        BlsTable blsTable = this.getById(tableId); // 内部会检查是否存在并抛异常

        // 如果状态未改变，可以提前返回避免数据库操作
        if (Objects.equals(blsTable.getStatus(), status)) {
            log.info("桌台 {} 状态已经是 {}，无需更新。", tableId, status);
            return;
        }

        blsTable.setStatus(status);
        boolean success = this.updateById(blsTable);
        if (!success) {
            throw BilliardsException.of(ResultCode.DATABASE_ERROR, "更新桌台状态失败");
        }
    }

    @Override
    public IPage<BlsTable> getTablePage(TableQueryRequest tableQueryRequest) {
        LambdaQueryWrapper<BlsTable> queryWrapper = Wrappers.lambdaQuery();
        // 如果 storeId 为空，当前行为是查询所有桌台（如果 storeId 为空）(后续根据业务决定是否应该抛异常或返回空分页？)
        queryWrapper.eq(StringUtils.isNotEmpty(tableQueryRequest.getStoreId()), BlsTable::getStoreId, tableQueryRequest.getStoreId());
        queryWrapper.eq(tableQueryRequest.getStatus() != null, BlsTable::getStatus, tableQueryRequest.getStatus());
        queryWrapper.like(StringUtils.isNotEmpty(tableQueryRequest.getTableNumber()), BlsTable::getTableNumber, tableQueryRequest.getTableNumber());
        queryWrapper.like(StringUtils.isNotEmpty(tableQueryRequest.getTableType()), BlsTable::getTableType, tableQueryRequest.getTableType());
        // 使用新字段排序：先按前缀排序，再按数字排序
        queryWrapper.orderByAsc(BlsTable::getTablePrefix)
            .orderByAsc(BlsTable::getTableNumeric);

        Page<BlsTable> tablePage = new Page<>(tableQueryRequest.getPageNumber(), tableQueryRequest.getPageSize());

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
        List<BlsTable> existingBlsTables = this.listTablesByStore(storeId);
        // 提取所有已存在的前缀和数字组合
        Set<String> existingCombinations = new HashSet<>();
        for (BlsTable blsTable : existingBlsTables) {
            if (blsTable.getTablePrefix() != null && blsTable.getTableNumeric() != null) {
                existingCombinations.add(blsTable.getTablePrefix() + blsTable.getTableNumeric());
            }
        }

        List<BlsTable> tablesToCreate = new java.util.ArrayList<>();
        int startNumeric = request.getStartNumeric();
        int count = request.getCount();

        for (int i = 0; i < count; i++) {
            int numeric = startNumeric + i;
            String combinedNumber = tablePrefix + numeric;

            // 检查组合是否已存在
            if (existingCombinations.contains(combinedNumber)) {
                throw BilliardsException.of(ResultCode.PARAM_ERROR, "桌台编号 " + combinedNumber + " 已存在");
            }

            BlsTable blsTable = new BlsTable();
            blsTable.setStoreId(storeId);
            blsTable.setTablePrefix(tablePrefix);
            blsTable.setTableNumeric(numeric);
            blsTable.setTableNumber(combinedNumber); // 保持向后兼容
            blsTable.setTableType(request.getTableType());
            blsTable.setPriceRuleId(request.getPriceRuleId());
            blsTable.setStatus(TableStatusEnum.FREE.getCode());  // 默认状态为空闲

            tablesToCreate.add(blsTable);
        }

        // 批量保存
        boolean success = this.saveBatch(tablesToCreate);
        if (!success) {
            throw BilliardsException.of(ResultCode.DATABASE_ERROR, "批量创建桌台失败");
        }

        // 为每个桌台生成二维码
        for (BlsTable blsTable : tablesToCreate) {
            generateQrcode(blsTable.getId());
        }
    }

    @Override
    public BlsTable getTableByQrcode(String qrcodeUrl) {
        if (StringUtils.isEmpty(qrcodeUrl)) {
            throw BilliardsException.of(ResultCode.PARAM_ERROR, "二维码URL不能为空");
        }

        LambdaQueryWrapper<BlsTable> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(BlsTable::getQrcodeUrl, qrcodeUrl);

        BlsTable blsTable = this.getOne(queryWrapper);
        if (blsTable == null) {
            throw BilliardsException.of(ResultCode.TABLE_NOT_EXIST, "通过二维码未找到对应桌台");
        }
        return blsTable;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BlsTable generateQrcode(String tableId) {
        if (StringUtils.isEmpty(tableId)) {
            throw BilliardsException.of(ResultCode.PARAM_ERROR, "桌台ID不能为空");
        }

        BlsTable blsTable = this.getById(tableId); // getById 内部会检查是否存在并抛异常

        try {
            // 1. 生成二维码内容：采用短码签名，避免直接暴露ID
            String qrCodeContent = qrCodeTokenService.generateContentForTable(blsTable.getId());

            // 2. 使用ZXing生成二维码图片
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(qrCodeContent, BarcodeFormat.QR_CODE, 300, 300);
            BufferedImage qrImage = MatrixToImageWriter.toBufferedImage(bitMatrix);

            // 3. 将BufferedImage转换为字节数组
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(qrImage, "png", baos);
            byte[] imageBytes = baos.toByteArray();

            // 4. 上传二维码图片并获取资源ID
            String fileName = "qrcode_" + blsTable.getTableNumber() + ".png";
            Map<String, String> uploadResult = fileService.uploadBytes(imageBytes, fileName, ResourceType.QRCODE);
            String resourceId = uploadResult.get("resourceId");

            // 5. 更新桌台信息，存储resourceId
            blsTable.setQrcodeUrl(resourceId);
            boolean success = this.updateById(blsTable);
            if(!success){
                throw BilliardsException.of(ResultCode.DATABASE_ERROR, "更新二维码信息失败");
            }

            log.info("为桌台 {} 生成/更新了二维码资源ID: {}", tableId, resourceId);
            return blsTable;
        } catch (WriterException | IOException e) {
            log.error("生成二维码失败: {}", e.getMessage(), e);
            throw BilliardsException.of(ResultCode.ERROR, "生成二维码失败");
        }
    }

    @Override
    public TableVO getTableInfo(String id) {
        // 获取桌台信息、价格信息
        BlsTable blsTable = this.getById(id);
        if (blsTable == null) {
            throw BilliardsException.of(ResultCode.TABLE_NOT_EXIST);
        }
        TableVO tableVO = tableConvert.toVo(blsTable);// 转换桌台为VO

        BlsPriceRule blsPriceRule = priceRuleService.getById(blsTable.getPriceRuleId());

        tableVO.setPriceRule(priceRuleConvert.toVo(blsPriceRule)); // 设置价格规则VO
        return tableVO;
    }

    @Override
    public BlsTable getTableInfoByQrcode(String qrcodeUrl) {
        if (StringUtils.isEmpty(qrcodeUrl)) {
            throw BilliardsException.of(ResultCode.PARAM_ERROR, "二维码URL不能为空");
        }
        // getTableByQrcode 内部已添加异常处理
        return this.getTableByQrcode(qrcodeUrl);
    }

    @Override
    public List<BlsTable> listTablesByStore(String storeId) {
        LambdaQueryWrapper<BlsTable> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(BlsTable::getStoreId, storeId);
        // 使用新字段排序：先按前缀排序，再按数字排序
        queryWrapper.orderByAsc(BlsTable::getTablePrefix)
                   .orderByAsc(BlsTable::getTableNumeric);

        return this.list(queryWrapper);
    }

    @Override
    public boolean releaseTable(String tableId) {

        if (StringUtils.isEmpty(tableId)) {
            throw BilliardsException.of(ResultCode.PARAM_ERROR, "桌台ID不能为空");
        }
        // 获取桌台信息
        BlsTable blsTable = this.getById(tableId);
        if (blsTable == null) {
            throw BilliardsException.of(ResultCode.TABLE_NOT_EXIST);
        }
        // 检查当前状态是否为锁定状态
        if (blsTable.getStatus() != TableStatusEnum.IN_USE.getCode()) { // 假设1表示锁定状态
            log.warn("桌台 {} 当前状态为 {}, 无需解锁", tableId, TableStatusEnum.fromCode(blsTable.getStatus()).getDescription());
            return true; // 如果不是锁定状态，直接返回成功
        }
        // 更新状态为0（空闲）
        blsTable.setStatus(TableStatusEnum.FREE.getCode());
        return this.updateById(blsTable); // 返回更新结果
    }

    /**
     * 锁定桌台（使用乐观锁确保原子性）
     * 注意：此方法应在分布式锁保护下调用，确保同一桌台的操作串行化
     *
     * @param tableId 桌台ID
     * @return 锁定后的桌台信息
     */
    @Override
    public BlsTable lockTable(String tableId) {
        if (StringUtils.isEmpty(tableId)) {
            throw BilliardsException.of(ResultCode.PARAM_ERROR);
        }
        // 获取桌台信息
        BlsTable blsTable = this.getById(tableId);
        if (blsTable == null) {
            throw BilliardsException.of(ResultCode.TABLE_NOT_EXIST);
        }
        // 检查当前状态是否为空闲状态
        if (blsTable.getStatus() != TableStatusEnum.FREE.getCode()) { // 假设0表示空闲状态
            log.warn("桌台 {} 当前状态为 {}, 无法锁定", tableId, TableStatusEnum.fromCode(blsTable.getStatus()).getDescription());
            throw BilliardsException.of(ResultCode.TABLE_OCCUPIED);
        }
        // 使用条件更新 + 乐观锁：只更新状态为FREE的记录，并检查版本号
        // 这样可以确保原子性：如果状态已被修改，更新不会成功
        LambdaUpdateWrapper<BlsTable> updateWrapper = Wrappers.lambdaUpdate();
        updateWrapper.eq(BlsTable::getId, tableId)
                     .eq(BlsTable::getStatus, TableStatusEnum.FREE.getCode()) // 只更新状态为FREE的记录
                     .set(BlsTable::getStatus, TableStatusEnum.LOCKED.getCode());
        boolean success = this.update(updateWrapper);

        if (!success) {
            // 更新失败，可能是状态已被修改或版本号冲突，重新查询最新状态
            BlsTable latestTable = this.getById(tableId);
            if (latestTable == null) {
                throw BilliardsException.of(ResultCode.TABLE_NOT_EXIST);
            }
            // 如果状态已经改变，说明桌台已被占用
            if (latestTable.getStatus() != TableStatusEnum.FREE.getCode()) {
                log.warn("桌台 {} 状态已被修改为 {}, 无法锁定", tableId, TableStatusEnum.fromCode(latestTable.getStatus()).getDescription());
                throw BilliardsException.of(ResultCode.TABLE_OCCUPIED);
            }
            // 如果状态仍然是FREE但更新失败，可能是版本号冲突，抛出异常
            throw BilliardsException.of(ResultCode.TABLE_OCCUPIED, "桌台状态更新失败，请重试");
        }

        // 更新成功后，重新查询最新状态（包含版本号）
        BlsTable updatedTable = this.getById(tableId);
        return updatedTable != null ? updatedTable : blsTable;
    }

    @Override
    public String randomTableId() {
        // 随机返回一个空闲桌台
        LambdaQueryWrapper<BlsTable> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(BlsTable::getStatus, TableStatusEnum.FREE.getCode()); // 只考虑空闲桌台
        queryWrapper.last("ORDER BY RAND() LIMIT 1"); // 随机排序并限制返回1条
        BlsTable blsTable = this.getOne(queryWrapper);
        return blsTable.getId();
    }

    @Override
    public List<ReservationTableVO> getReservationTables(String storeId, String date) {
        if (StringUtils.isEmpty(storeId)) {
            throw BilliardsException.of(ResultCode.PARAM_ERROR, "门店ID不能为空");
        }
        if (StringUtils.isEmpty(date)) {
            date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        }

        // 解析日期
        LocalDate targetDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        LocalDateTime dayStart = targetDate.atStartOfDay();
        LocalDateTime dayEnd = targetDate.atTime(23, 59, 59);

        // 获取预约配置
        BlsReserveConfig config = reservationConfigService.getConfig();
        String openingHours = config.getOpeningHours(); // 格式：10:00-23:00

        // 查询门店下的所有桌台
        List<BlsTable> tables = getTablesByStore(storeId);

        // 查询该日期所有桌台的预约记录
        List<BlsReservation> reservations = reservationService.list(
            Wrappers.lambdaQuery(BlsReservation.class)
                .eq(BlsReservation::getStoreId, storeId)
                .eq(BlsReservation::getStatus, ReservationStatusEnum.PENDING.getCode())
                .ge(BlsReservation::getStartTime, DateUtils.toDate(dayStart))
                .le(BlsReservation::getStartTime, DateUtils.toDate(dayEnd))
        );

        // 按桌台ID分组预约记录
        Map<String, List<BlsReservation>> reservationsByTable = reservations.stream()
            .collect(Collectors.groupingBy(BlsReservation::getTableId));

        // 转换桌台列表
        return tables.stream().map(table -> {
            ReservationTableVO vo = new ReservationTableVO();
            vo.setId(table.getId());
            vo.setName("桌台 " + table.getTableNumber());

            // 设置状态文本
            if (table.getStatus() == TableStatusEnum.FREE.getCode()) {
                vo.setStatusText("可预约");
            } else{
                vo.setStatusText(TableStatusEnum.fromCode(table.getStatus()).getDescription() + "(不可用)");
            }

            vo.setDescription(table.getDescription());
            vo.setImage(table.getImage());

            // 设置 mock tags（暂时返回两个）
            List<TableTagVO> tags = new ArrayList<>();
            tags.add(new TableTagVO());
            tags.get(0).setType("disinfection");
            tags.get(0).setText("时间段消毒");
            tags.add(new TableTagVO());
            tags.get(1).setType("holiday");
            tags.get(1).setText("节假日优惠");
            vo.setTags(tags);

            // 生成时间段
            List<BlsReservation> tableReservations = reservationsByTable.getOrDefault(table.getId(), Collections.emptyList());
            vo.setSlots(generateTimeSlots(dayStart, openingHours, tableReservations));

            return vo;
        }).collect(Collectors.toList());
    }

    /**
     * 生成时间段列表
     * @param dayStart 一天的开始时间（00:00:00）
     * @param openingHours 营业时间（格式：10:00-23:00）
     * @param reservations 该桌台在该天的预约记录
     * @return 时间段列表
     */
    private List<TimeSlotVO> generateTimeSlots(LocalDateTime dayStart, String openingHours, List<BlsReservation> reservations) {
        List<TimeSlotVO> slots = new ArrayList<>();
        
        // 获取当前时间
        LocalDateTime now = LocalDateTime.now();
        // 判断是否是今天
        boolean isToday = dayStart.toLocalDate().equals(now.toLocalDate());

        // 解析营业时间
        int startHour = 10;
        int endHour = 23;
        if (StringUtils.isNotBlank(openingHours) && openingHours.contains("-")) {
            String[] parts = openingHours.split("-");
            if (parts.length == 2) {
                try {
                    startHour = Integer.parseInt(parts[0].split(":")[0]);
                    endHour = Integer.parseInt(parts[1].split(":")[0]);
                } catch (Exception e) {
                    log.warn("解析营业时间失败: {}", openingHours);
                }
            }
        }

        // 生成24小时的时间段
        for (int hour = 0; hour < 24; hour++) {
            TimeSlotVO slot = new TimeSlotVO();
            slot.setStartTime(String.format("%02d:00", hour));
            slot.setEndTime(String.format("%02d:00", hour + 1));
            slot.setLabel("");

            LocalDateTime slotStart = dayStart.plusHours(hour);
            LocalDateTime slotEnd = dayStart.plusHours(hour + 1);

            // 判断时间段是否在营业时间内
            if (hour < startHour || hour >= endHour) {
                slot.setStatus("blocked");
            } else {
                // 如果是今天，检查时间段是否已经过了当前时间
                if (isToday && slotStart.isBefore(now)) {
                    // 时间段的开始时间已经过了当前时间，标记为不可预约
                    slot.setStatus("blocked");
                } else {
                    // 检查是否有预约占用该时间段
                    boolean isReserved = false;
                    for (BlsReservation reservation : reservations) {
                        // 检查时间段是否与预约重叠
                        if (!(slotEnd.isBefore(reservation.getStartTime()) || slotStart.isAfter(reservation.getEndTime()))) {
                            isReserved = true;
                            break;
                        }
                    }

                    slot.setStatus(isReserved ? "blocked" : "available");
                }
            }

            slots.add(slot);
        }

        return slots;
    }

}
