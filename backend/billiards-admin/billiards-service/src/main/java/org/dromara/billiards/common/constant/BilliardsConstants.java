package org.dromara.billiards.common.constant;

import java.math.BigDecimal;

/**
 * @Description
 * @Author zhangsip
 * @Version V1.0.0
 * @Since 1.0
 * @Date 2025/4/22
 */
public interface BilliardsConstants {

    /**
     * 60分
     */
    BigDecimal MINUTES_PER_HOUR = new BigDecimal("60");

    /**
     * 订单前缀
     */
    String ORDER_PREFIX = "BL";

    /**
     * 订单前缀
     */
    String APPLY_NO_PREFIX = "AP";

    /**
     * 登录用户key
     */
    String LOGIN_USER_KEY = "loginUser";

    /**
     * 登录商家key
     */
    String LOGIN_MERCHANT_KEY = "loginMerchant";

    /**
     * 桌台状态：空闲
     */
    Integer TABLE_STATUS_IDLE = 0;

    /**
     * 桌台状态：使用中
     */
    Integer TABLE_STATUS_USING = 1;

    /**
     * 桌台状态：维修中
     */
    Integer TABLE_STATUS_MAINTENANCE = 2;

    /**
     * 桌台状态：锁定
     */
    Integer TABLE_STATUS_LOCKED = 3;

    /**
     * 桌台类型：普通
     */
    Integer TABLE_TYPE_NORMAL = 1;

    /**
     * 桌台类型：专业
     */
    Integer TABLE_TYPE_PROFESSIONAL = 2;

    /**
     * 桌台类型：大师
     */
    Integer TABLE_TYPE_MASTER = 3;

    /**
     * 订单状态：进行中
     */
    Integer ORDER_STATUS_PROCESSING = 0;

    /**
     * 订单状态：已完成
     */
    Integer ORDER_STATUS_COMPLETED = 1;

    /**
     * 订单状态：已取消
     */
    Integer ORDER_STATUS_CANCELLED = 2;

    /**
     * 支付状态：未支付
     */
    Integer PAYMENT_STATUS_UNPAID = 0;

    /**
     * 支付状态：已支付
     */
    Integer PAYMENT_STATUS_PAID = 1;

    /**
     * 支付状态：已退款
     */
    Integer PAYMENT_STATUS_REFUNDED = 2;

    /**
     * 规则类型：标准计费
     */
    Integer RULE_TYPE_STANDARD = 1;

    /**
     * 规则类型：阶梯计费
     */
    Integer RULE_TYPE_LADDER = 2;

    /**
     * 会员标志：非会员
     */
    Integer IS_MEMBER_NO = 0;

    /**
     * 会员标志：会员
     */
    Integer IS_MEMBER_YES = 1;

    /**
     *  台球管理数据源
     */
    String DS_BILLIARDS_PLATFORM = "platform";

    /**
     * 最低消费
     */
    BigDecimal MIN_BALANCE_FOR_PLAY = new BigDecimal("10.00");
}
