package org.dromara.billiards.service;

import org.dromara.billiards.domain.bo.TableDto;
import org.dromara.billiards.domain.bo.BatchTableRequest;
import org.dromara.billiards.domain.bo.TableQueryRequest;
import org.dromara.billiards.domain.entity.BlsTable;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.dromara.billiards.domain.vo.TableVO;

import java.util.List;

/**
 * 桌台服务接口
 */
public interface TableService extends IService<BlsTable> {

    /**
     * 根据门店ID查询桌台列表
     * @param storeId 门店ID
     * @return 桌台列表
     */
    List<BlsTable> getTablesByStore(String storeId);

    /**
     * 创建桌台
     * @param tableDto 桌台信息
     * @return 创建后的桌台实体
     */
    BlsTable createTable(TableDto tableDto);

    /**
     * 更新桌台
     * @param blsTable 桌台信息
     */
    void updateTable(BlsTable blsTable);

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
    IPage<BlsTable> getTablePage(TableQueryRequest tableQueryRequest);

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
    BlsTable getTableByQrcode(String qrcodeUrl);

    /**
     * 生成桌台二维码
     * @param tableId 桌台ID
     * @return 桌台信息（含二维码）
     */
    BlsTable generateQrcode(String tableId);

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
    BlsTable getTableInfoByQrcode(String qrcodeUrl);

    /**
     * 获取门店桌台列表
     * @param storeId
     * @return
     */
    List<BlsTable> listTablesByStore(String storeId);

    /**
     * 解锁桌台
     * @param tableId
     * @return
     */
    boolean releaseTable(String tableId);


    /**
     * 扫码之后，获取可用桌台，按照正常逻辑不会有多个人对同一个桌台扫码，但是还是需要限制：同一张桌台不可能被两个人同时打开
     * @param tableId
     * @return
     */
    BlsTable lockTable(String tableId);
}
