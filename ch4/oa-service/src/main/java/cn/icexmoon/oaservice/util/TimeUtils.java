package cn.icexmoon.oaservice.util;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * @ClassName TimeUtils
 * @Description
 * @Author icexmoon@qq.com
 * @Date 2025/6/4 下午7:08
 * @Version 1.0
 */
public class TimeUtils {
    /**
     * 将日期转换为时间
     *
     * @param date 日期
     * @return 时间
     */
    public static LocalDateTime dateToLocalDateTime(Date date) {
        return date.toInstant()                    // Date → Instant（时间戳）
                .atZone(ZoneId.systemDefault())    // 附加系统默认时区 → ZonedDateTime
                .toLocalDateTime();                // 剥离时区 → LocalDateTime
    }

    /**
     * 获取日志的开始查询时间（0小时0分0秒）
     *
     * @param date 日期
     * @return 开始查询时间
     */
    public static LocalDateTime toStartTime(Date date) {
        if (date == null)
            return null;
        LocalDateTime startTime = TimeUtils.dateToLocalDateTime(date);
        startTime = startTime.withHour(0).withMinute(0).withSecond(0).withNano(0);
        return startTime;
    }

    /**
     * 获取日期的结束查询时间（23小时59分59秒）
     *
     * @param date 日期
     * @return 结束查询时间
     */
    public static LocalDateTime toEndTime(Date date) {
        if (date == null)
            return null;
        LocalDateTime endTime = TimeUtils.dateToLocalDateTime(date);
        endTime = endTime.withHour(23).withMinute(59).withSecond(59).withNano(0);
        return endTime;
    }
}
