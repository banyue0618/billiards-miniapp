package org.dromara.billiards.common.constant;

public enum PaymentStatus {
    /**
     * 未支付
     */
    UNPAID(0, "未支付-待查询"),

    /**
     * 支付中
     */
    PAYING(1, "支付中"),

    /**
     * 支付成功
     */
    PAID(2, "支付成功"),

    /**
     * 支付失败
     */
    PAY_FAIL(4, "支付失败"),

    /**
     * 已退款
     */
    REFUNDED(8, "已退款"),

    /**
     * 已退款
     */
    REFUNDING(16, "退款中"),

    /**
     * 取消支付
     */
    PAY_CANCEL(32, "取消支付"),

    /**
     * 支付超时
     */
    PAY_TIME(32, "支付超时");

    private final int code;
    private final String description;

    PaymentStatus(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
