package org.dromara.common.pay.util;

import cn.hutool.core.util.StrUtil;
import com.xkzhangsan.time.converter.DateTimeConverterUtil;
import com.xkzhangsan.time.formatter.DateTimeFormatterUtil;

import java.time.ZonedDateTime;
import java.util.Date;

/**
 * @Description
 * @Author zhangsip
 * @Version V1.0.0
 * @Since 1.0
 * @Date 2025/6/2
 */
public class DateTimeZoneUtil {

    private static final long serialVersionUID = -1331008203306650395L;

    /**
     * 时间转 TimeZone
     * <p>
     * 2020-08-17T16:46:37+08:00
     *
     * @param time 时间戳
     * @return {@link String}  TimeZone 格式时间字符串
     * @throws Exception 异常信息
     */
    public static String dateToTimeZone(long time) throws Exception {
        return dateToTimeZone(new Date(time));
    }

    /**
     * 时间转 TimeZone
     * <p>
     * 2020-08-17T16:46:37+08:00
     *
     * @param date {@link Date}
     * @return {@link String} TimeZone 格式时间字符串
     * @throws Exception 异常信息
     */
    public static String dateToTimeZone(Date date) throws Exception {
        String time;
        if (date == null) {
            throw new Exception("date is not null");
        }
        ZonedDateTime zonedDateTime = DateTimeConverterUtil.toZonedDateTime(date);
        time = DateTimeFormatterUtil.format(zonedDateTime, DateTimeFormatterUtil.YYYY_MM_DD_T_HH_MM_SS_XXX_FMT);
        return time;
    }

    /**
     * TimeZone 时间转标准时间
     * <p>
     * 2020-08-17T16:46:37+08:00 to 2020-08-17 16:46:37
     *
     * @param str TimeZone格式时间字符串
     * @return {@link String} 标准时间字符串
     * @throws Exception 异常信息
     */
    public static String timeZoneDateToStr(String str) throws Exception {
        String time;
        if (StrUtil.isBlank(str)) {
            throw new Exception("str is not null");
        }
        ZonedDateTime zonedDateTime = DateTimeFormatterUtil.parseToZonedDateTime(str, DateTimeFormatterUtil.YYYY_MM_DD_T_HH_MM_SS_XXX_FMT);
        if (zonedDateTime == null) {
            throw new Exception("str to zonedDateTime fail");
        }
        time = zonedDateTime.format(DateTimeFormatterUtil.YYYY_MM_DD_HH_MM_SS_FMT);
        return time;
    }


}
