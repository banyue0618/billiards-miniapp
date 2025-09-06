package org.dromara.billiards.mapper;

import org.dromara.billiards.domain.entity.BlsTable;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 桌台Mapper接口
 */
@Mapper
public interface TableMapper extends BaseMapper<BlsTable> {

    /**
     * 根据门店ID查询桌台列表
     */
    List<BlsTable> selectByStoreId(@Param("storeId") String storeId);

    /**
     * 根据桌台编号和门店ID查询桌台
     */
    BlsTable selectByTableNumber(@Param("storeId") String storeId, @Param("tableNumber") String tableNumber);

    /**
     * 分页查询门店桌台列表
     */
    IPage<BlsTable> selectTablePage(Page<?> page, @Param("storeId") String storeId);

    /**
     * 更新桌台状态
     */
    int updateStatus(@Param("id") String id, @Param("status") Integer status);

    /**
     * 根据二维码URL查询桌台
     */
    BlsTable selectByQrcodeUrl(@Param("qrcodeUrl") String qrcodeUrl);

    /**
     * 批量更新桌台的计费规则
     * @param tableIds 桌台ID列表
     * @param priceRuleId 计费规则ID
     * @return 更新的记录数
     */
    int batchUpdatePriceRule(@Param("tableIds") List<String> tableIds, @Param("priceRuleId") String priceRuleId);
}
