package com.loopeer.android.apps.lreader.utilities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.format.Time;

import org.geometerplus.ReaderApp;
import org.geometerplus.android.fbreader.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by YuGang Yang on Apr 29, 2014.
 * Copyright 2007-2015 Loopeer.com. All rights reserved.
 */
public class TimeDateUtils {

    public static int expiredDay(final long borrowTime, int expiredDay) {
        int daysBetween = daysBetween(borrowTime);
        return expiredDay - daysBetween;
    }

    public static int daysBetween(long returnDate) {
        final long now = System.currentTimeMillis();
        return daysBetween(now, returnDate);
    }

    public static int daysBetween(long now, long returnDate) {
        return daysBetween(new Date(now), new Date(returnDate));
    }

    public static int daysBetween(Date now, Date returnDate) {
        Calendar cNow = Calendar.getInstance();
        cNow.clear();
        Calendar cReturnDate = Calendar.getInstance();
        cReturnDate.clear();
        cNow.setTime(now);
        cReturnDate.setTime(returnDate);
        setTimeToMidnight(cNow);
        setTimeToMidnight(cReturnDate);
        long todayMs = cNow.getTimeInMillis();
        long returnMs = cReturnDate.getTimeInMillis();
        long intervalMs = todayMs - returnMs;
        return millisecondsToDays(intervalMs);
    }

    private static int millisecondsToDays(long intervalMs) {
        return (int) (intervalMs / (1000 * 86400));
    }

    private static void setTimeToMidnight(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
    }

    /**
     * 借阅剩余时间
     * @param borrowTime
     * @return
     */
    public static int getExpiredDay(final long borrowTime, int expiredDay) {
        long timeInterval =  System.currentTimeMillis() - borrowTime;
        final long second = 1000;
        final long minute = second * 60;
        final long hour = minute * 60;
        final long day = hour * 24;
        int intervalDays = (int) (timeInterval / day);
        return expiredDay - intervalDays;
    }

    // time : 十位时间戳
    public static String getFormatTime(final long time) {
        final Context context = ReaderApp.getAppContext();
        String returnStr = "";

        long timeInterval = System.currentTimeMillis() - time * 1000;

        final long second = 1000;
        final long minute = second * 60;
        final long hour = minute * 60;
        final long day = hour * 24;
        final long week = day * 7;
        final long month = day * 30;

        int intervalMonths = (int) (timeInterval / month);
        int intervalWeeks = (int) (timeInterval / week) ;
        int intervalDays = (int) (timeInterval / day);
        int intervalHours = (int) (timeInterval / hour);
        int intervalMinutes = (int) (timeInterval / minute);
        int intervalSeconds = (int) (timeInterval / second);

        if (intervalMonths > 12) { // 1年前
            returnStr = context.getString(R.string.one_year_before);
        } else if (intervalMonths > 0 ) { // %d月前
            returnStr = intervalMonths + context.getString(R.string.month) + context.getString(R.string.before);
        } else if (intervalWeeks > 0 ) { // %d周前
            returnStr = intervalWeeks + context.getString(R.string.week) + context.getString(R.string.before);
        } else if (intervalDays > 0 ) { // %d天前
            returnStr = intervalDays + context.getString(R.string.day) + context.getString(R.string.before);
        } else if (intervalHours > 0) {
            returnStr = intervalHours + context.getString(R.string.hour) + context.getString(R.string.before);
        } else if (intervalMinutes > 0) {
            returnStr = intervalMinutes + context.getString(R.string.minute) + context.getString(R.string.before);
        } else if (intervalSeconds > 3) {
            returnStr = intervalSeconds + context.getString(R.string.second) + context.getString(R.string.before);
        } else {
            returnStr = "刚刚";
        }

        return returnStr;
    }
    
    @SuppressLint("SimpleDateFormat")
    public static  String formateDate(long date){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(new Date(date*1000 ));
        
    }

    @SuppressLint("SimpleDateFormat")
    public static  String formateDateMd(long date){
        SimpleDateFormat sdf = new SimpleDateFormat("MM月dd日");
        return sdf.format(new Date(date*1000));

    }
    
    @SuppressLint("SimpleDateFormat")
    public static  String formateDateYmd(long date){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(new Date(date*1000));
        
    }
    
    @SuppressLint("SimpleDateFormat")
    private static String formatYMDdotDate(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd", Locale.US);
        String str = dateFormat.format(date);
        return str;
    }
    
    public static String FormatCouponTime(long time) {
        Date date = new Date(time*1000);
        return formatYMDdotDate(date);
    }

    public static  String getWelcomeDate(long date){
        SimpleDateFormat sdf = new SimpleDateFormat("MM.dd.yy EEEE");
        return sdf.format(new Date(date));

    }
    
    // 赞列表时间显示格式
    public static String getLikeFormatTime(final long time) {
        final Context context = ReaderApp.getAppContext();
        String returnStr = "";

        long timeInterval = System.currentTimeMillis() - time * 1000;

        final long second = 1000;
        final long minute = second * 60;
        final long hour = minute * 60;
        final long day = hour * 24;
        final long month = day * 30;

        int intervalMonths = (int) (timeInterval / month);
        int intervalDays = (int) (timeInterval / day);
        int intervalHours = (int) (timeInterval / hour);
        int intervalMinutes = (int) (timeInterval / minute);
        int intervalSeconds = (int) (timeInterval / second);

        if ( intervalMonths > 0 ) { // %d月前
            returnStr = intervalMonths + context.getString(R.string.month) + context.getString(R.string.before);
        } else if ( intervalDays > 0) { 
            returnStr = intervalDays + context.getString(R.string.day) + context.getString(R.string.before);
        } else if (intervalHours > 0) { 
            returnStr = intervalHours + context.getString(R.string.hour) + context.getString(R.string.before);
        } else if (intervalMinutes > 0) {
            returnStr += intervalMinutes
                    + context.getString(R.string.minute) + context.getString(R.string.before);
        } else if (intervalSeconds > 0) {
            returnStr += intervalSeconds + context.getString(R.string.second) + context.getString(R.string.before);
        }

        return returnStr;
    }

    private static boolean isSameDay(Time lhs, Time rhs) {
        return (lhs.year == rhs.year) 
                && (lhs.month == rhs.month) 
                && (lhs.monthDay == rhs.monthDay);
        
    }
    private static boolean isToday(Time current, Time time) {
        return isSameDay(current, time);
    }
    
    public static Time getYesterday() {
        Time current = new Time();
        current.setToNow();
        long time = current.normalize(true) - 24 * 3600 * 1000; 
        Time yesterday = new Time();
        yesterday.set(time);
        yesterday.normalize(true);
        return yesterday;
    }
    
    private static boolean isYesterday(Time time) {
        Time yesterday = getYesterday();
        return isSameDay(time, yesterday);
    }
    
    private static boolean isInThisWeek(Time current, Time time) {
        return (current.year == time.year) 
                && (current.getWeekNumber() == time.getWeekNumber()); 
    }
    
    public static String formatTimeForMessage(long time) {
        Time messageTime = new Time();
        messageTime.set(time * 1000);
        Time current = new Time();
        current.setToNow();
        String text = null;
        if (isToday(current, messageTime)) {
            String clock = messageTime.format(" %0I:%0M");
            if (messageTime.hour < 12) {
                text = ReaderApp.getInstance().getString(R.string.morning) + clock;
            } else {
                text = ReaderApp.getInstance().getString(R.string.afternoon) + clock;
            }
        } else if (isYesterday(messageTime)) {
            text = ReaderApp.getInstance().getString(R.string.yesterday);
            text = text + messageTime.format(" %0H:%0M");
        } else if (isInThisWeek(current, messageTime)) {
            text = messageTime.format("%A %0H:%0M");
        } else {
            text = messageTime.format("%Y-%0m-%0d %0H:%0M");
        }
        
        return text;
    }

}
