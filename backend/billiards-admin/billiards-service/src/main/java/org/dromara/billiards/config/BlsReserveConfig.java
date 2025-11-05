package org.dromara.billiards.config;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 预约功能配置类
 * 配置从数据库 sys_config 表加载，每个租户有独立的配置实例
 * 
 * @Description
 * @Author zhangsip
 * @Version V1.0.0
 * @Since 1.0
 * @Date 2025/11/3
 */
@Data
public class BlsReserveConfig {

    // ==================== 时间相关配置 ====================
    
    /**
     * 每次最短预约时长（分钟）
     */
    private Integer minDurationMinutes = 30;

    /**
     * 每次最长预约时长（分钟）
     */
    private Integer maxDurationMinutes = 120;

    /**
     * 用户可提前预约天数（自然日）
     */
    private Integer advanceReserveDays = 3;

    /**
     * 预约开始后多少分钟未签到则自动过期并释放（分钟）
     */
    private Integer expireAfterStartMinutes = 5;

    /**
     * 开始前多久发送提醒（分钟）
     */
    private Integer remindBeforeStartMinutes = 15;

    /**
     * 预约时间接近阈值（分钟）
     * 如果线下扫码时，距离预约开始时间小于等于此阈值，则不允许开台
     */
    private Integer tooCloseThresholdMinutes = 30;

    // ==================== 支付/定金相关配置 ====================
    
    /**
     * 是否启用定金模式
     */
    private Boolean requireDeposit = false;

    /**
     * 定金金额（元）
     */
    private BigDecimal depositAmount = BigDecimal.valueOf(10.00);

    /**
     * 退款策略（如：before_30min、always、never）
     */
    private String depositRefundPolicy = "before_30min";

    /**
     * 开始前多少分钟内取消不可退
     */
    private Integer refundGracePeriodMinutes = 30;

    // ==================== 用户限制类配置 ====================
    
    /**
     * 每个用户每日最多预约次数
     */
    private Integer maxPerDay = 2;

    /**
     * 用户同时存在的进行中预约上限
     */
    private Integer maxPendingReservations = 1;

    /**
     * 连续爽约多少次后禁止预约
     */
    private Integer banAfterNoShowCount = 3;

    /**
     * 封禁时长（天）
     */
    private Integer banDaysAfterNoShow = 7;

    // ==================== 门店/资源层面配置 ====================
    
    /**
     * 门店是否开启预约功能
     */
    private Boolean enableForStore = true;

    /**
     * 单次最多可预约的桌数
     */
    private Integer maxTablesPerUser = 1;

    /**
     * 时间粒度（分钟为一个单位）
     */
    private Integer timeUnitMinutes = 30;

    /**
     * 预约可选时间段（格式：10:00-23:00）
     */
    private String openingHours = "10:00-23:00";

    // ==================== 通知与提醒配置 ====================
    
    /**
     * 是否开启预约提醒
     */
    private Boolean enableReminder = true;

    /**
     * 微信模板消息ID
     */
    private String reminderTemplateId = "WX123456";

    /**
     * 提前多久提醒（分钟）
     */
    private Integer reminderBeforeMinutes = 15;

    /**
     * 是否推送取消通知
     */
    private Boolean notifyOnCancel = true;

    /**
     * 取消通知微信模板消息ID
     */
    private String cancelNotifyTemplateId = "WX123456";

    // ==================== 后台/系统行为配置 ====================
    
    /**
     * 后台定时任务执行间隔（分钟）
     */
    private Integer autoCheckIntervalMinutes = 5;

    /**
     * 管理员是否可手动调整预约
     */
    private Boolean allowAdminOverride = true;

    /**
     * 是否记录预约操作日志
     */
    private Boolean auditLogEnabled = true;
}
