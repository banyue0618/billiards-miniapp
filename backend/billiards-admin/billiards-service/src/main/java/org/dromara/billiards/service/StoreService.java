package org.dromara.billiards.service;

import org.dromara.billiards.domain.entity.BlsStore;
import org.dromara.billiards.domain.vo.NearbyStoreVO;
import org.dromara.billiards.domain.bo.StoreDto;
import org.dromara.billiards.domain.bo.StoreQueryRequest;
import org.dromara.billiards.domain.bo.StatusRequest;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.math.BigDecimal;
import java.util.List;

/**
 * 门店服务接口
 */
public interface StoreService extends IService<BlsStore> {

    /**
     * 新增门店
     *
     * @param storeDto 门店创建数据
     * @return 创建后的门店实体
     */
    BlsStore createStore(StoreDto storeDto);

    /**
     * 更新门店信息
     *
     * @param id 门店ID (来自路径变量)
     * @param storeDto 门店更新数据
     * @return 更新后的门店实体
     */
    BlsStore updateStore(String id, StoreDto storeDto);

    /**
     * 获取门店详情（后端）
     * @param id 门店ID
     * @return 门店信息
     */
    BlsStore getStoreInfo(String id);

    /**
     * 获取门店详情（小程序）
     * @param id 门店ID
     * @return 门店信息
     */
    NearbyStoreVO getStoreInfo(String id, BigDecimal latitude, BigDecimal longitude);

    /**
     * 更新门店状态和公告
     * @param request 包含门店ID、新状态和新公告的数据请求对象
     * @return 是否成功
     */
    boolean updateStoreStatusAndAnnouncement(StatusRequest request);

    /**
     * 获取门店桌台数量
     * @param storeId 门店ID
     * @return 桌台数量
     */
    Long getTableCount(String storeId);

    /**
     * 批量删除门店
     * @param ids 门店ID数组
     * @return 是否成功
     */
    boolean batchDelete(String[] ids);

    /**
     * 获取附近门店列表
     * @param latitude 纬度
     * @param longitude 经度
     * @param radius 搜索半径(公里)
     * @return 门店列表（包含距离信息）
     */
    List<NearbyStoreVO> getNearbyStores(BigDecimal latitude, BigDecimal longitude, Integer radius);

    /**
     * 分页查询门店列表
     *
     * @param request 查询参数
     * @return 门店分页实体列表
     */
    IPage<BlsStore> pageStores(StoreQueryRequest request);

    /**
     * 删除单个门店
     * @param id 门店ID
     * @return 是否成功
     */
    boolean deleteStore(String id);

    // --- Methods for Miniapp ---

    /**
     * (小程序) 获取所有可用门店列表 (status=0)
     * @return 可用门店实体列表
     */
    List<BlsStore> listAvailableStores();

    /**
     * (小程序) 分页查询可用门店列表 (status=0, keyword search)
     * @param request 查询参数 (包含分页和关键词)
     * @return 可用门店分页实体列表
     */
    IPage<BlsStore> pageAvailableStores(StoreQueryRequest request);
}
