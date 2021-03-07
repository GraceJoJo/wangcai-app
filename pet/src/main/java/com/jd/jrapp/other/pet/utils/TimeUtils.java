package com.jd.jrapp.other.pet.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.Date;

/**
 * author : JOJO
 * e-mail : zhoujuan26@jd.com
 * date   : 2021/3/7 4:44 PM
 * desc   :
 */
public class TimeUtils {

    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;

    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;
    private static final String REFRESH_NAME = "REFRESH_KEY";
    private static final String REFRESH_TIME_KEY = "REFRESH_TIME_KEY";
    /**
     * 获取上次刷新保存的时间
     * @param context
     * @return
     */
    public static long getLastRefreshTime(Context context){
        @SuppressLint("WrongConstant")
        SharedPreferences s =context.getSharedPreferences(REFRESH_NAME, Context.MODE_APPEND);
        return s.getLong(REFRESH_TIME_KEY,new Date().getTime());
    }

    /**
     * 保存本次刷新的时间
     * @param refreshTime
     */
    public static void saveLastRefreshTime(Context context,long refreshTime){
        @SuppressLint("WrongConstant")
        SharedPreferences s = context.getSharedPreferences(REFRESH_NAME,Context.MODE_APPEND);
        s.edit().putLong(REFRESH_TIME_KEY,refreshTime).commit();
    }
    /**
     * 按照毫秒来存储
     *
     * @param time
     * @return
     */
    public static String getTimeAgo(long time) {
        if (time < 1000000000000L) {
            // if timestamp given in seconds, convert to millis
            time *= 1000;
        }

        long now = System.currentTimeMillis();
        if (time > now || time <= 0) {
            return "未知时间";
        }

        final long diff = now - time;

        if (diff < MINUTE_MILLIS) {
            return "刚刚";
        } else if (diff < 2 * MINUTE_MILLIS) {
            return "1分钟前";
        } else if (diff < 50 * MINUTE_MILLIS) {
            return diff / MINUTE_MILLIS + "分钟前";
        } else if (diff < 90 * MINUTE_MILLIS) {
            return "1小时前";
        } else if (diff < 24 * HOUR_MILLIS) {
            return diff / HOUR_MILLIS + "小时前";
        } else if (diff < 48 * HOUR_MILLIS) {
            return "昨天";
        } else {
            return diff / DAY_MILLIS + "天前";
        }
    }
}
