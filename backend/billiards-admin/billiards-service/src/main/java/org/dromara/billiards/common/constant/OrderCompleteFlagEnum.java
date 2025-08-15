package org.dromara.billiards.common.constant;

public enum OrderCompleteFlagEnum {

    // 完成标记 1-用户结束 2-管理员结束 3-超时结束
    USER_END(1, "用户结束"),
    ADMIN_END(2, "管理员结束"),
    TIMEOUT_END(3, "超时结束");
    private final Integer code;
    private final String desc;
    OrderCompleteFlagEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    public Integer getCode() {
        return code;
    }
    public String getDesc() {
        return desc;
    }
    public static String getDescByCode(Integer code) {
        for (OrderCompleteFlagEnum value : OrderCompleteFlagEnum.values()) {
            if (value.getCode().equals(code)) {
                return value.getDesc();
            }
        }
        return null;
    }

}
