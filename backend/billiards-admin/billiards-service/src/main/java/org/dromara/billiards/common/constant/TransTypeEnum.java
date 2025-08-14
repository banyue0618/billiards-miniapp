package org.dromara.billiards.common.constant;

public enum TransTypeEnum {

    // 交易类型:RECHARGE/CONSUME/REFUND
    RECHARGE("RECHARGE", "充值"),
    CONSUME("CONSUME", "消费"),
    REFUND("REFUND", "退款");
    private final String code;
    private final String desc;
    TransTypeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static TransTypeEnum getByCode(String code) {
        for (TransTypeEnum type : TransTypeEnum.values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }

}
