package org.dromara.billiards.service;

import org.dromara.billiards.config.BlsReserveConfig;
import org.dromara.common.tenant.helper.TenantHelper;

/**
 * 预约配置服务接口
 * 负责从数据库加载配置并映射到配置类
 *
 * @Description
 * @Author zhangsip
 * @Version V1.0.0
 * @Since 1.0
 * @Date 2025/11/3
 */
public interface ReservationConfigService {

    /**
     * 获取当前租户的预约配置
     *
     * @return 预约配置对象
     */
    BlsReserveConfig getConfig();

    /**
     * 获取指定租户的预约配置
     *
     * @param tenantId 租户ID
     * @return 预约配置对象
     */
    BlsReserveConfig getConfig(String tenantId);

    /**
     * 刷新指定租户的配置缓存
     *
     * @param tenantId 租户ID，如果为null则刷新当前租户
     */
    void refreshConfig(String tenantId);

    /**
     * 刷新所有租户的配置缓存
     */
    void refreshAllConfig();
}

