package org.dromara.billiards.common.constant;

public enum MemberChangeTypeEnum {

    // 变更类型 NEW/RENEWAL/UPGRADE/EXPIRED

    NEW("NEW", "新购"),
    RENEWAL("RENEWAL", "续费"),
    UPGRADE("UPGRADE", "升级"),
    EXPIRED("EXPIRED", "过期降级");

    private final String code;
    private final String description;

    MemberChangeTypeEnum(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }


    public static MemberChangeTypeEnum fromCode(String code) {
        for (MemberChangeTypeEnum type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown code: " + code);
    }
}
