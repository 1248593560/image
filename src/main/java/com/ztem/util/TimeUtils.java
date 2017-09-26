package com.ztem.util;

import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by LiMeiyuan on 2017/6/8.
 * 时间工具类
 */
public class TimeUtils {

    private static final int SECOND_MILLIS = 1000;

    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;

    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;

    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    private static final SimpleDateFormat datetimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private static final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");

    public static String getTime(Date date) {
        return datetimeFormat.format(date);
    }

    public static String getTimeAgo(long time) {
        if (time < 1000000000000L) {
            // if timestamp given in seconds, convert to millis
            time *= 1000;
        }

        long now = new Date().getTime();
        if (time > now || time <= 0) {
            return null;
        }

        final long diff = now - time;
        if (diff < MINUTE_MILLIS) {
            return "刚刚";
        } else if (diff < 2 * MINUTE_MILLIS) {
            return "1分钟前";
        } else if (diff < 60 * MINUTE_MILLIS) {
            return diff / MINUTE_MILLIS + "分钟前";
        } else if (diff < 120 * MINUTE_MILLIS) {
            return "一小时前";
        } else if (diff < 24 * HOUR_MILLIS) {
            return diff / HOUR_MILLIS + "小时前";
        } else if (diff < 48 * HOUR_MILLIS) {
            return "一天前";
        } else {
            return diff / DAY_MILLIS + "天前";
        }
    }

    public static Date joinDatetime(Date date, Date time) throws ParseException {
        String dateStr = dateFormat.format(date);
        String timeStr;
        if (time == null) {
            timeStr = "00:00:00";
        } else {
            timeStr = timeFormat.format(time);
        }
        return datetimeFormat.parse(dateStr + " " + timeStr);
    }

    public static Date joinDatetime(Date date, String time) throws ParseException {
        String dateStr = dateFormat.format(date);
        String timeStr;
        if (time == null || "".equals(time.trim())) {
            timeStr = "00:00:00";
        } else {
            timeStr = time;
        }
        return datetimeFormat.parse(dateStr + " " + timeStr);
    }

    /**
     * @title: 转换秒为时分秒
     * @author ltf
     * @description:
     * @date 2015年11月5日 下午1:29:22
     * @param durationSeconds
     * @return
     * @throws
     */
    public static String getDuration(int durationSeconds) {
        int hours = durationSeconds / (60 * 60);
        int leftSeconds = durationSeconds % (60 * 60);
        int minutes = leftSeconds / 60;
        int seconds = leftSeconds % 60;

        StringBuffer sBuffer = new StringBuffer();
        if (hours != 0) {
            sBuffer.append(hours);
            sBuffer.append("时");
        }
        if (minutes != 0) {
            sBuffer.append(minutes);
            sBuffer.append("分");
        }
        if (seconds != 0) {
            sBuffer.append(seconds);
            sBuffer.append("秒");
        }
        return StringUtils.isNotBlank(sBuffer.toString()) ? sBuffer.toString() : "0";
    }


    /**
     * 格式化时间
     * @param format
     * @param dateStr
     * @return
     */
    public static  Date formatDate(String format,String dateStr) {
        SimpleDateFormat sdf=new SimpleDateFormat(format);
        Date date = new Date();
        try {
            date= sdf.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return  date;
    }


}
