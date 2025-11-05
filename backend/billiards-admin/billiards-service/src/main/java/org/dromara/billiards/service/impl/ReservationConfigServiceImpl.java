package org.dromara.billiards.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.billiards.common.constant.BilliardsConstants;
import org.dromara.billiards.config.BlsReserveConfig;
import org.dromara.billiards.service.ReservationConfigService;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.tenant.helper.TenantHelper;
import org.dromara.system.domain.vo.SysConfigVo;
import org.dromara.system.service.ISysConfigService;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 预约配置服务实现类
 * 从数据库加载配置并映射到配置类，支持多租户和缓存
 *
 * @Description
 * @Author zhangsip
 * @Version V1.0.0
 * @Since 1.0
 * @Date 2025/11/3
 */
@Slf4j
@Service
@RequiredArgsConstructor
@DS(BilliardsConstants.DS_ADMIN)
public class ReservationConfigServiceImpl implements ReservationConfigService {

    private static final String CACHE_NAME = "reservation_config";
    private static final String CONFIG_PREFIX = "reservation.";

    private final ISysConfigService sysConfigService;
    private final CacheManager cacheManager;

    /**
     * 获取当前租户的预约配置
     *
     * @return 预约配置对象
     */
    @Override
    @Cacheable(value = CACHE_NAME, key = "#root.method.name + ':' + T(org.dromara.common.tenant.helper.TenantHelper).getTenantId()")
    public BlsReserveConfig getConfig() {
        String tenantId = TenantHelper.getTenantId();
        return getConfig(tenantId);
    }

    /**
     * 获取指定租户的预约配置
     *
     * @param tenantId 租户ID
     * @return 预约配置对象
     */
    @Override
    @Cacheable(value = CACHE_NAME, key = "'getConfig:' + #tenantId")
    public BlsReserveConfig getConfig(String tenantId) {

        BlsReserveConfig config = new BlsReserveConfig();

        // 使用租户上下文加载配置
        return TenantHelper.dynamic(tenantId, () -> {
            // 根据前缀批量加载相关配置
            List<SysConfigVo> sysConfigVos = sysConfigService.selectConfigListByKeyLike(tenantId, CONFIG_PREFIX);

            // 本地映射，避免后续重复查询
            final Map<String, String> configMap = sysConfigVos.stream()
                .collect(Collectors.toMap(SysConfigVo::getConfigKey, SysConfigVo::getConfigValue, (a, b) -> a));

            mergeConfig( configMap , config);

            return config;
        });
    }

    /**
     * 刷新指定租户的配置缓存
     *
     * @param tenantId 租户ID，如果为null则刷新当前租户
     */
    @Override
    public void refreshConfig(String tenantId) {
        if (StringUtils.isBlank(tenantId)) {
            tenantId = TenantHelper.getTenantId();
        }

        if (StringUtils.isBlank(tenantId)) {
            log.warn("租户ID为空，无法刷新配置缓存");
            return;
        }

        // 重新从数据库加载配置并缓存
        List<SysConfigVo> sysConfigVos = sysConfigService.selectConfigListByKeyLike(tenantId, CONFIG_PREFIX);

        // 本地映射，避免后续重复查询
        final Map<String, String> configMap = sysConfigVos.stream()
            .collect(Collectors.toMap(SysConfigVo::getConfigKey, SysConfigVo::getConfigValue, (a, b) -> a));

        BlsReserveConfig config = new BlsReserveConfig();

        mergeConfig( configMap , config);

        // 更新缓存中的该租户配置
        Cache cache = cacheManager.getCache(CACHE_NAME);
        if (cache != null) {
            cache.put("getConfig:" + tenantId, config);
        }

        log.info("已刷新租户 {} 的预约配置缓存，重新从数据库加载", tenantId);
    }

    /**
     * 刷新所有租户的配置缓存
     */
    @Override
    public void refreshAllConfig() {

        // 清除预约配置缓存
        Cache cache = cacheManager.getCache(CACHE_NAME);
        if (cache != null) {
            cache.clear();
        }

        log.info("已清除所有租户的预约配置缓存（下次获取时将重新从数据库加载）");
    }

    private void mergeConfig(Map<String, String> configMap, BlsReserveConfig config) {
        // ==================== 时间相关配置 ====================
        config.setMinDurationMinutes(getInt(configMap, "min_duration_minutes", 30));
        config.setMaxDurationMinutes(getInt(configMap, "max_duration_minutes", 120));
        config.setAdvanceReserveDays(getInt(configMap, "advance_reserve_days", 3));
        config.setExpireAfterStartMinutes(getInt(configMap, "expire_after_start_minutes", 5));
        config.setRemindBeforeStartMinutes(getInt(configMap, "remind_before_start_minutes", 15));
        config.setTooCloseThresholdMinutes(getInt(configMap, "too_close_threshold_minutes", 30));

        // ==================== 支付/定金相关配置 ====================
        config.setRequireDeposit(getBool(configMap, "require_deposit", false));
        config.setDepositAmount(getDecimal(configMap, "deposit_amount", BigDecimal.valueOf(10.00)));
        config.setDepositRefundPolicy(getString(configMap, "deposit_refund_policy", "before_30min"));
        config.setRefundGracePeriodMinutes(getInt(configMap, "refund_grace_period_minutes", 30));

        // ==================== 用户限制类配置 ====================
        config.setMaxPerDay(getInt(configMap, "max_per_day", 2));
        config.setMaxPendingReservations(getInt(configMap, "max_pending_reservations", 1));
        config.setBanAfterNoShowCount(getInt(configMap, "ban_after_no_show_count", 3));
        config.setBanDaysAfterNoShow(getInt(configMap, "ban_days_after_no_show", 7));

        // ==================== 门店/资源层面配置 ====================
        config.setEnableForStore(getBool(configMap, "enable_for_store", true));
        config.setMaxTablesPerUser(getInt(configMap, "max_tables_per_user", 1));
        config.setTimeUnitMinutes(getInt(configMap, "time_unit_minutes", 30));
        config.setOpeningHours(getString(configMap, "opening_hours", "10:00-23:00"));

        // ==================== 通知与提醒配置 ====================
        config.setEnableReminder(getBool(configMap, "enable_reminder", true));
        config.setReminderTemplateId(getString(configMap, "reminder_template_id", "WX123456"));
        config.setReminderBeforeMinutes(getInt(configMap, "reminder_before_minutes", 15));
        config.setNotifyOnCancel(getBool(configMap, "notify_on_cancel", true));
        config.setCancelNotifyTemplateId(getString(configMap, "cancel_notify_template_id", "WX123456"));

        // ==================== 后台/系统行为配置 ====================
        config.setAutoCheckIntervalMinutes(getInt(configMap, "auto_check_interval_minutes", 5));
        config.setAllowAdminOverride(getBool(configMap, "allow_admin_override", true));
        config.setAuditLogEnabled(getBool(configMap, "audit_log_enabled", true));
    }


    private String getString(Map<String, String> map, String keySuffix, String defaultValue) {
        String value = map.get(CONFIG_PREFIX + keySuffix);
        return (value == null || value.trim().isEmpty()) ? defaultValue : value.trim();
    }

    private int getInt(Map<String, String> map, String keySuffix, int defaultValue) {
        String value = map.get(CONFIG_PREFIX + keySuffix);
        if (value == null || value.trim().isEmpty()) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private boolean getBool(Map<String, String> map, String keySuffix, boolean defaultValue) {
        String value = map.get(CONFIG_PREFIX + keySuffix);
        if (value == null) {
            return defaultValue;
        }
        String v = value.trim().toLowerCase();
        if ("true".equals(v) || "1".equals(v) || "yes".equals(v) || "y".equals(v)) {
            return true;
        }
        if ("false".equals(v) || "0".equals(v) || "no".equals(v) || "n".equals(v)) {
            return false;
        }
        return defaultValue;
    }

    private BigDecimal getDecimal(Map<String, String> map, String keySuffix, BigDecimal defaultValue) {
        String value = map.get(CONFIG_PREFIX + keySuffix);
        if (value == null || value.trim().isEmpty()) {
            return defaultValue;
        }
        try {
            return new BigDecimal(value.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}

