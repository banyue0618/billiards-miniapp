package org.dromara.billiards.common.utils;

import org.dromara.billiards.common.constant.BilliardsConstants;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

/**
 * 申请单号生成工具类
 */
public class OrderNumberGenerator {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    private static final Random RANDOM = new Random();

    /**
     * 生成订单号
     * 格式：年月日时分秒+6位随机数
     * @return 订单号
     */
    public static String generate() {
        // 获取当前时间
        String timeStr = LocalDateTime.now().format(DATE_TIME_FORMATTER);

        // 生成6位随机数
        int randomNum = RANDOM.nextInt(900000) + 100000;

        // 组合成订单号
        return timeStr + randomNum;
    }

    /**
     * 生成带前缀的订单号
     * @return 带前缀的订单号
     */
    public static String generateWithPrefix() {
        return BilliardsConstants.ORDER_PREFIX + generate();
    }
}
