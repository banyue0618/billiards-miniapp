package org.dromara.billiards.common.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

/**
 * 预约编号生成工具类
 *
 * @author banyue
 * @date 2025-11-03
 */
public class ReservationNumberGenerator {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    private static final Random RANDOM = new Random();

    /**
     * 生成预约编号
     * 格式：年月日时分秒+6位随机数
     * @return 预约编号
     */
    public static String generate() {
        // 获取当前时间
        String timeStr = LocalDateTime.now().format(DATE_TIME_FORMATTER);

        // 生成6位随机数
        int randomNum = RANDOM.nextInt(900000) + 100000;

        // 组合成预约编号
        return "RV" + timeStr + randomNum;
    }
}

