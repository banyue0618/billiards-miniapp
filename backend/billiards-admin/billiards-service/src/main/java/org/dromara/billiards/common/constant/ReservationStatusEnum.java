package org.dromara.billiards.common.constant;

/**
 * 预约状态枚举
 * 状态：0=预约中,1=已到店,2=已完成,3=已取消,4=已过期
 *
 * @author banyue
 * @date 2025-11-03
 */
public enum ReservationStatusEnum {

    PENDING(0L, "预约中"),
    CHECKED_IN(1L, "已到店"),
    COMPLETED(2L, "已完成"),
    CANCELLED(3L, "已取消"),
    EXPIRED(4L, "已过期");

    private final Long code;
    private final String description;

    ReservationStatusEnum(Long code, String description) {
        this.code = code;
        this.description = description;
    }

    public Long getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static ReservationStatusEnum fromCode(Long code) {
        if (code == null) {
            return null;
        }
        for (ReservationStatusEnum status : ReservationStatusEnum.values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown ReservationStatusEnum code: " + code);
    }
}

