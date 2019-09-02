package com.stevexls.imagepicker.utils;

import android.content.Context;

/**
 * Time：2019/5/8 14:34
 * Description:
 */
public class ScreenUtils {
    public static int getScreenWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }
}
