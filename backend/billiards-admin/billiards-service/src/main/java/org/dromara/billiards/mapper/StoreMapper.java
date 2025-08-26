package org.dromara.billiards.mapper;

import org.dromara.billiards.domain.entity.Store;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 门店Mapper接口
 */
@Mapper
public interface StoreMapper extends BaseMapper<Store> {
    String findTenantIdByStoreId(String storeId);
}
