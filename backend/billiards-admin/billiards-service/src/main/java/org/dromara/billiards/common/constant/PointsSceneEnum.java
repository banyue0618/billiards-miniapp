package org.dromara.billiards.common.constant;

import java.util.Arrays;

public enum PointsSceneEnum {

    // 积分场景：获取1消费 2签到 3活动 4评价 5首次绑定 6邀请；消耗21抵扣 22兑换商品 23兑换券

    GET_CONSUME(1L, "消费"),
    GET_SIGN_IN(2L, "签到"),
    GET_ACTIVITY(3L, "活动"),
    GET_EVALUATE(4L, "评价"),
    GET_FIRST_BIND(5L, "首次绑定"),
    GET_INVITE(6L, "邀请"),
    CONSUME_DEDUCT(21L, "抵扣"),
    CONSUME_EXCHANGE_PRODUCT(22L, "兑换商品"),
    CONSUME_EXCHANGE_COUPON(23L, "兑换券");

    private final Long code;
    private final String name;

    PointsSceneEnum(Long code, String name) {
        this.code = code;
        this.name = name;
    }

    public Long getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public static PointsSceneEnum getByCode(String code) {
        return Arrays.stream(PointsSceneEnum.values())
                .filter(item -> item.getCode().equals(code))
                .findFirst()
                .orElse(null);
    }
}
