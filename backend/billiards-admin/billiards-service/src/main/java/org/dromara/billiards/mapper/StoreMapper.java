package org.dromara.billiards.mapper;

import org.dromara.billiards.domain.entity.BlsStore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 门店Mapper接口
 */
@Mapper
public interface StoreMapper extends BaseMapper<BlsStore> {
    String findTenantIdByStoreId(String storeId);
}
