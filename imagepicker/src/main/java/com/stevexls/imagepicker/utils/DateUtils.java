package com.stevexls.imagepicker.utils;

/**
 * Time：2019/5/16 18:26
 * Description:
 */
public class DateUtils {

    /**
     * long型时长转为String
     * @param duration 毫秒时长
     * @return
     */
    public static String toStrDuration(long duration) {
        long minute = duration / 1000 / 60;
        long second = (duration - minute * 60 * 1000) / 1000;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(minute > 0 ? String.valueOf(minute) : "0");
        stringBuilder.append(":");
        stringBuilder.append(second > 0 ? (second > 10 ? String.valueOf(second) : "0" + second) : "00");
        return stringBuilder.toString();
    }
}
