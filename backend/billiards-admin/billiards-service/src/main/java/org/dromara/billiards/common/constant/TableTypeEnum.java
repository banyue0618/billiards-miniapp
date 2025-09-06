package org.dromara.billiards.common.constant;

public enum TableTypeEnum {

    // 1-普通 2-专业 3-大师
    NORMAL(1, "普通级"),
    PROFESSIONAL(2, "专业级"),
    MASTER(3, "大师级");
    private final Integer code;
    private final String desc;
    TableTypeEnum(Integer code, String desc) {
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
        for (TableTypeEnum value : TableTypeEnum.values()) {
            if (value.getCode().equals(code)) {
                return value.getDesc();
            }
        }
        return null;
    }
}
