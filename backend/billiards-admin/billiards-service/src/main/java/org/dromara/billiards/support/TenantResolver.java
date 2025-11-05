package org.dromara.billiards.support;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.dromara.billiards.domain.entity.BlsStore;
import org.dromara.billiards.service.IBlsPayChannelConfigService;
import org.dromara.billiards.domain.bo.BlsPayChannelConfigBo;
import org.dromara.billiards.domain.vo.BlsPayChannelConfigVo;
import org.dromara.billiards.service.StoreService;
import org.dromara.common.tenant.helper.TenantHelper;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
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

    private final StoreService storeService;
    private final CacheManager cacheManager;

    private Cache getCache(String name) {
        return cacheManager.getCache(name);
    }

    public TenantMerchant resolveTenantMerchant(String storeId) {
        // 仅使用 storeId 解析（聚合小程序不允许用 appId 推断租户）
        if (StringUtils.isNotBlank(storeId)) {
            Cache tCache = getCache("tenant_by_store");
            Cache mCache = getCache("merchant_by_store");
            if (tCache != null && mCache != null) {
                String tCached = tCache.get(storeId, String.class);
                String mCached = mCache.get(storeId, String.class);
                if (StringUtils.isNotBlank(tCached)) {
                    return new TenantMerchant(tCached, mCached);
                }
            }
            TenantMerchant tm = TenantHelper.ignore(() -> {
                BlsStore blsStore = storeService.getById(storeId);
                if (blsStore == null) return null;
                return new TenantMerchant(blsStore.getTenantId(), blsStore.getMerchantId());
            });
            if (tm != null) {
                if (tCache != null && StringUtils.isNotBlank(tm.tenantId())) {
                    tCache.put(storeId, tm.tenantId());
                }
                if (mCache != null && StringUtils.isNotBlank(tm.merchantId())) {
                    mCache.put(storeId, tm.merchantId());
                }
            }
            return tm;
        }
        return null;
    }

    public record TenantMerchant(String tenantId, String merchantId) implements java.io.Serializable {
    }

}
