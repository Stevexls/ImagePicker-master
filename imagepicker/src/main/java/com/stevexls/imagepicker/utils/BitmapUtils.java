package com.stevexls.imagepicker.utils;

import android.app.Activity;
import android.content.ContentResolver;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.Uri;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Time：2019/7/24 15:23
 * Description:
 */
public class BitmapUtils {

    public static Point getBitmapSize(Uri uri, Activity activity) {
        if (activity == null) {
            return new Point(0, 0);
        }
        ContentResolver resolver = activity.getContentResolver();
        InputStream is = null;
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            is = resolver.openInputStream(uri);
            BitmapFactory.decodeStream(is, null, options);
            int width = options.outWidth;
            int height = options.outHeight;
            return new Point(width, height);
        } catch (FileNotFoundException e) {
            return new Point(0, 0);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
