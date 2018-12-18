package com.cmd.wallet.common.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static SimpleDateFormat sdf_time = new SimpleDateFormat("HH:mm:ss");
    /**
     * 获取日期字符串
     *
     * @param time
     *            Date
     * @return yyyy-MM-dd
     */
    public static String getDateTimeString(Date time) {
        return sdf.format(time);
    }
    public static String getTimeString(Date time){
        return sdf_time.format(time);
    }

    public static Date getDate(String date) {
        if (date == null || (date.length() != 10 && date.length() != 19)) {
            return null;
        }
        try {
            return sdf.parse(date);
        } catch (ParseException e) {
            return null;
        }
    }

    public static int getDayBeginTimestamp(long timeMillis) {
        // 计算当天0点的时间
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeMillis);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        Date todayBegin = calendar.getTime();
        int todayBeginTimestamp = (int)(todayBegin.getTime()/1000);
        return todayBeginTimestamp;
    }

    public static Date getDate(String date,String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        try {
            return sdf.parse(date);
        } catch (ParseException e) {
            return null;
        }
    }
    public static Date addMin(Date time,Integer min) {
        Calendar c = Calendar.getInstance();
        c.setTime(time);
        c.add(Calendar.MINUTE, min);// 今天+1分钟
        return c.getTime();
    }
    public static String getDateTimeString(Date time,String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format(time);
    }
    public static String format(Date date, String pattern) {
        if (date != null) {
            SimpleDateFormat df = new SimpleDateFormat(pattern);
            return df.format(date);
        }
        return null;
    }

    //根据时间分秒获取时间
    public static Date getDateByString(String dateStr){
        String currentTimeStr = getDateYMDStr(new Date());
        String dateStrs = currentTimeStr +" "+dateStr;
        SimpleDateFormat sDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); //加上时间
        Date date = null;
        try {
            date=sDateFormat.parse(dateStrs);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return  date;
    }

    //根据时间类型获取年月日
    public static String getDateYMDStr(Date date){
        SimpleDateFormat sDateFormat=new SimpleDateFormat("yyyy-MM-dd "); //加上时间
        String datestr=sDateFormat.format(date);
        return  datestr;
    }



}
