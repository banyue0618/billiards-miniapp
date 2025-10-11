package org.dromara.billiards.mapper;

import org.apache.ibatis.annotations.Param;
import org.dromara.billiards.domain.entity.BlsPayChannelConfig;
import org.dromara.billiards.domain.vo.BlsPayChannelConfigVo;
import org.dromara.common.mybatis.core.mapper.BaseMapperPlus;

/**
 * 支付服务商配置(门店>商户>租户)Mapper接口
 *
 * @author banyue
 * @date 2025-08-28
 */
public interface BlsPayChannelConfigMapper extends BaseMapperPlus<BlsPayChannelConfig, BlsPayChannelConfigVo> {

    String selectMerchantIdByStoreId(@Param("storeId") String storeId, @Param("appId") String appid, @Param("tenantId") String tenantId, @Param("merchantId") String merchantId);
}
