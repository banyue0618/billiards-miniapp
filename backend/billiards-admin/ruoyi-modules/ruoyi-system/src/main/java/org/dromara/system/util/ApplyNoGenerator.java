package org.dromara.system.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

/**
 * @Description
 * @Author zhangsip
 * @Version V1.0.0
 * @Since 1.0
 * @Date 2025/9/5
 */
public class ApplyNoGenerator {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    private static final Random RANDOM = new Random();

    /**
     * 生成申请单号
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
}
