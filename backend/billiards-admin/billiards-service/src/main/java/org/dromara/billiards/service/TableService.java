package org.dromara.billiards.service;

import org.dromara.billiards.domain.bo.TableDto;
import org.dromara.billiards.domain.bo.BatchTableRequest;
import org.dromara.billiards.domain.bo.TableQueryRequest;
import org.dromara.billiards.domain.entity.Table;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.dromara.billiards.domain.vo.TableVO;

import java.util.List;

/**
 * 桌台服务接口
 */
public interface TableService extends IService<Table> {

    /**
     * 根据门店ID查询桌台列表
     * @param storeId 门店ID
     * @return 桌台列表
     */
    List<Table> getTablesByStore(String storeId);

    /**
     * 创建桌台
     * @param tableDto 桌台信息
     * @return 创建后的桌台实体
     */
    Table createTable(TableDto tableDto);

    /**
     * 更新桌台
     * @param table 桌台信息
     */
    void updateTable(Table table);

    /**
     * 删除桌台
     * @param tableId 桌台ID
     */
    void deleteTable(String tableId);

    /**
     * 批量删除桌台
     * @param ids 桌台ID列表
     */
    void batchDeleteTables(List<String> ids);

    /**
     * 更新桌台状态
     * @param tableId 桌台ID
     * @param status 状态值
     */
    void updateStatus(String tableId, Integer status);

    /**
     * 分页查询门店桌台
     * @param tableQueryRequest 分页参数
     * @return 分页结果
     */
    IPage<Table> getTablePage(TableQueryRequest tableQueryRequest);

    /**
     * 批量创建桌台
     * @param request 批量创建请求
     */
    void batchCreateTables(BatchTableRequest request);

    /**
     * 根据二维码获取桌台
     * @param qrcodeUrl 二维码URL
     * @return 桌台信息
     */
    Table getTableByQrcode(String qrcodeUrl);

    /**
     * 生成桌台二维码
     * @param tableId 桌台ID
     * @return 桌台信息（含二维码）
     */
    Table generateQrcode(String tableId);

    // --- Methods for Miniapp with specific error handling ---

    /**
     * (小程序) 获取桌台详情，未找到则抛出异常
     * @param id 桌台ID
     * @return 桌台实体
     */
    TableVO getTableInfo(String id);

    /**
     * (小程序) 根据二维码获取桌台，未找到则抛出异常
     * @param qrcodeUrl 二维码URL
     * @return 桌台实体
     */
    Table getTableInfoByQrcode(String qrcodeUrl);

    /**
     * 获取门店桌台列表
     * @param storeId
     * @return
     */
    List<Table> listTablesByStore(String storeId);
}
