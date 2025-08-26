package org.dromara.billiards.config;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.dromara.billiards.mapper.StoreMapper;
import org.dromara.common.tenant.helper.TenantHelper;
import org.springframework.stereotype.Component;

/**
 * @Description
 * @Author zhangsip
 * @Version V1.0.0
 * @Since 1.0
 * @Date 2025/8/20
 */
@Component
@RequiredArgsConstructor
public class TenantResolver {

    private final StoreMapper storeMapper;

    public String resolveTenantId(String storeId, String appId) {
        // storeId
        if (StringUtils.isNotBlank(storeId)) {
            return TenantHelper.ignore(() -> storeMapper.findTenantIdByStoreId(storeId)); // 返回tenant_id
        }
        return null;
    }

}
