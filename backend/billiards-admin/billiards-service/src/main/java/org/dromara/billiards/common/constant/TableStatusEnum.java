package org.dromara.billiards.common.constant;

public enum TableStatusEnum {

    // 状态 0-空闲 1-使用中 2-维修中 3-锁定 4-已预约
    FREE(0, "空闲"),
    IN_USE(1, "使用中"),
    UNDER_MAINTENANCE(2, "维修中"),
    LOCKED(3, "锁定"),
    RESERVED(4, "已预约");

    private final int code;

    private final String description;

    TableStatusEnum(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static TableStatusEnum fromCode(int code) {
        for (TableStatusEnum status : TableStatusEnum.values()) {
            if (status.code == code) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown TableStatusEnum code: " + code);
    }


}
