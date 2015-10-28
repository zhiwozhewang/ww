package com.longyuan.qm.utils;

import android.annotation.SuppressLint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 类名：TimeUtil.java 类描述：时间处理工具
 *
 * @author wader 创建时间：2011-12-02 11:03
 */
public class TimeUtil {
    public final static String FORMAT_DATE = "yyyy.MM.dd";
    public final static String FORMAT_TIME = "HH:mm";
    public final static String FORMAT_DATE_TIME = "yyyy.MM.dd HH:mm";
    public final static String FORMAT_MONTH_DAY_TIME = "MM月dd日 HH:mm";
    public final static String FORMAT_MONTH_DAY_TIME2 = "MM.dd HH:mm";
    private static final int YEAR = 365 * 24 * 60 * 60;// 年
    private static final int MONTH = 30 * 24 * 60 * 60;// 月
    private static final int DAY = 24 * 60 * 60;// 天
    private static final int HOUR = 60 * 60;// 小时
    private static final int MINUTE = 60;// 分钟
    @SuppressLint("SimpleDateFormat")
    private static SimpleDateFormat sdf = new SimpleDateFormat();

    /**
     * 根据时间戳获取描述性时间，如3分钟前，1天前
     *
     * @param timestamp 时间戳 单位为毫秒
     * @return 时间字符串
     */
    public static String getDescriptionTimeFromTimestamp(long timestamp) {
        long currentTime = System.currentTimeMillis();
        long timeGap = (currentTime - timestamp) / 1000;// 与现在时间相差秒数
        String timeStr = null;
        if (timeGap > YEAR) {
            timeStr = timeGap / YEAR + "年前";
        } else if (timeGap > MONTH) {
            timeStr = timeGap / MONTH + "个月前";
        } else if (timeGap > DAY) {// 1天以上
            timeStr = timeGap / DAY + "天前";
        } else if (timeGap > HOUR) {// 1小时-24小时
            timeStr = timeGap / HOUR + "小时前";
        } else if (timeGap > MINUTE) {// 1分钟-59分钟
            timeStr = timeGap / MINUTE + "分钟前";
        } else {// 1秒钟-59秒钟
            timeStr = "刚刚";
        }
        return timeStr;
    }

    /**
     * 根据时间戳获取指定格式的时间，如2011-11-30 08:40
     *
     * @param timestamp 时间戳 单位为毫秒
     * @param format    指定格式 如果为null或空串则使用默认格式"yyyy-MM-dd HH:MM"
     * @return
     */
    public static String getFormatTimeFromTimestamp(long timestamp,
                                                    String format) {
        if (format == null || format.trim().equals("")) {
            sdf.applyPattern(FORMAT_DATE);
            int currentYear = Calendar.getInstance().get(Calendar.YEAR);
            int year = Utils.string2int(sdf.format(new Date(timestamp))
                    .substring(0, 4));
            if (currentYear == year) {// 如果为今年则不显示年份
                sdf.applyPattern(FORMAT_MONTH_DAY_TIME);
            } else {
                sdf.applyPattern(FORMAT_DATE_TIME);
            }
        } else {
            sdf.applyPattern(format);
        }
        Date date = new Date(timestamp);
        return sdf.format(date);
    }

    /**
     * 根据时间戳获取时间字符串，并根据指定的时间分割数partionSeconds来自动判断返回描述性时间还是指定格式的时间
     *
     * @param timestamp      时间戳 单位是毫秒
     * @param partionSeconds 时间分割线，当现在时间与指定的时间戳的秒数差大于这个分割线时则返回指定格式时间，否则返回描述性时间
     * @param format
     * @return
     */
    public static String getMixTimeFromTimestamp(long timestamp,
                                                 long partionSeconds, String format) {
        long currentTime = System.currentTimeMillis();
        long timeGap = (currentTime - timestamp) / 1000;// 与现在时间相差秒数
        if (timeGap <= partionSeconds) {
            return getDescriptionTimeFromTimestamp(timestamp);
        } else {
            return getFormatTimeFromTimestamp(timestamp, format);
        }
    }

    /**
     * 获取当前日期的指定格式的字符串
     *
     * @param format 指定的日期时间格式，若为null或""则使用指定的格式"yyyy-MM-dd HH:MM"
     * @return
     */
    public static String getCurrentTime(String format) {
        if (format == null || format.trim().equals("")) {
            sdf.applyPattern(FORMAT_DATE_TIME);
        } else {
            sdf.applyPattern(format);
        }
        return sdf.format(new Date());
    }

    /**
     * 将日期字符串以指定格式转换为Date
     *
     * @param time   日期字符串
     * @param format 指定的日期格式，若为null或""则使用指定的格式"yyyy-MM-dd HH:MM"
     * @return
     */
    public static Date getTimeFromString(String timeStr, String format) {
        if (format == null || format.trim().equals("")) {
            sdf.applyPattern(FORMAT_DATE_TIME);
        } else {
            sdf.applyPattern(format);
        }
        try {
            return sdf.parse(timeStr);
        } catch (ParseException e) {
//			WaderDebug.i("TimeUtil", "时间转换错误，现在一个当前时间");
            return new Date();
        }
    }

    /**
     * 将Date以指定格式转换为日期时间字符串
     *
     * @param date   日期
     * @param format 指定的日期时间格式，若为null或""则使用指定的格式"yyyy-MM-dd HH:MM"
     * @return
     */
    public static String getStringFromTime(Date time, String format) {

        if (format == null || format.trim().equals("")) {
            sdf.applyPattern(FORMAT_DATE_TIME);
        } else {
            sdf.applyPattern(format);
        }
        return sdf.format(time);
    }

    // /**
    // * 将String以指定格式转换为日期时间字符串
    // *
    // * @param time
    // * 格林时间
    // * @return
    // */
    // public static String getStringFromTime(String time) {
    // sdf.applyPattern(FORMAT_MONTH_DAY_TIME2);
    // try {
    // time = sdf.format(new Date(Long.parseLong(time + "000")));
    // } catch (Exception e) {
    // e.printStackTrace();
    // time = "";
    // }
    // return time;
    //
    // }

    /**
     * 获取日期字符串 格式：2013.01.24 上午/下午
     *
     * @param time
     * @return
     */
    public static String getTimeWithAP(String time, String format) {
        try {
            long timeStamp = Long.parseLong(time) * 1000;
            // long timeGap = (currentTime - timeStamp) / 1000;
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(timeStamp);
            sdf.applyPattern(format);
            time = sdf.format(timeStamp) + " "
                    + (c.get(Calendar.AM_PM) == Calendar.AM ? "上午" : "下午");
        } catch (Exception e) {
            e.printStackTrace();
            time = "";
        }
        return time;
    }

    /**
     * Modified by wader 2013-01-13 上午10:40 更改时间显示规则（今天、昨天、前天、更早的显示具体日期）
     * 将String以指定格式转换为日期时间字符串
     *
     * @param time 时间戳字符串 单位 ：秒
     * @return
     */
    public static String getStringFromTime(String time) {
        try {
            long timeStamp = Long.parseLong(time) * 1000;
            long currentTime = System.currentTimeMillis();
            // long timeGap = (currentTime - timeStamp) / 1000;
            Calendar c1 = Calendar.getInstance();
            c1.setTimeInMillis(timeStamp);
            int year1 = c1.get(Calendar.YEAR);
            int month1 = c1.get(Calendar.MONTH);

            Calendar c2 = Calendar.getInstance();
            c2.setTimeInMillis(currentTime);
            int year2 = c2.get(Calendar.YEAR);
            int month2 = c2.get(Calendar.MONTH);

            if (year2 > year1) {// 不同年
                sdf.applyPattern(FORMAT_DATE_TIME);
                time = sdf.format(timeStamp);
            } else if (month2 > month1) {// 同年不同月
                sdf.applyPattern(FORMAT_MONTH_DAY_TIME);
                time = sdf.format(timeStamp);
            } else {// 同月
                int day1 = c1.get(Calendar.DAY_OF_MONTH);
                int day2 = c2.get(Calendar.DAY_OF_MONTH);
                int dayGap = day2 - day1;

                if (dayGap <= 0) {// 同一天
                    sdf.applyPattern(FORMAT_TIME);
                    time = "今天 " + sdf.format(timeStamp);
                } else if (dayGap > 0 && dayGap <= 1) {
                    sdf.applyPattern(FORMAT_TIME);
                    time = "昨天 " + sdf.format(timeStamp);
                } else if (dayGap > 1 && dayGap <= 2) {
                    sdf.applyPattern(FORMAT_TIME);
                    time = "前天 " + sdf.format(timeStamp);
                } else if (dayGap > 2) {
                    sdf.applyPattern(FORMAT_MONTH_DAY_TIME2);
                    time = sdf.format(timeStamp);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            time = "";
        }
        return time;

    }

//	public static SameTime compareWithCurrent(long timestamp) {
//		SameTime st = new SameTime();
//		long currentTime = System.currentTimeMillis();
//		long timeGap = (currentTime - timestamp) / 1000;// 与现在时间相差秒数
//		if (timeGap > YEAR) {
//			// doNothing
//		} else if (timeGap > MONTH) {// 同年
//			st.setSameYear(true);
//		} else if (timeGap > DAY) {// 同月
//			st.setSameYear(true);
//			st.setSameMonth(true);
//		} else if (timeGap > HOUR) {// 同日
//			st.setSameYear(true);
//			st.setSameMonth(true);
//			st.setSameDay(true);
//		} else if (timeGap > MINUTE) {// 同小时
//			st.setSameYear(true);
//			st.setSameMonth(true);
//			st.setSameDay(true);
//			st.setSameHour(true);
//		} else if (timeGap > 1) {// 同分钟
//			st.setSameYear(true);
//			st.setSameMonth(true);
//			st.setSameDay(true);
//			st.setSameHour(true);
//			st.setSameMinute(true);
//		} else {// 同秒
//			st.setSameYear(true);
//			st.setSameMonth(true);
//			st.setSameDay(true);
//			st.setSameHour(true);
//			st.setSameMinute(true);
//			st.setSameSecond(true);
//		}
//		return st;
//	}
}
