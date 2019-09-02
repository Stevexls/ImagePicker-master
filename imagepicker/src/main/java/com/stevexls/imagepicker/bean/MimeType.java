package com.stevexls.imagepicker.bean;

import android.content.ContentResolver;
import android.support.v4.util.ArraySet;
import android.text.TextUtils;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Locale;
import java.util.Set;

/**
 * Time：2019/3/25 14:53
 * Description: 参考Matisse: https://github.com/zhihu/Matisse
 */
public enum MimeType {

    // images
    JPEG("image/jpeg", arraySetOf("jpg", "jpeg")),

    PNG("image/png", arraySetOf("png")),

    GIF("image/gif", arraySetOf("gif")),

    BMP("image/x-ms-bmp", arraySetOf("bmp")),

    WEBP("image/webp", arraySetOf("webp")),

    // videos
    MPEG("video/mpeg", arraySetOf("mpeg", "mpg")),

    MP4("video/mp4", arraySetOf("mp4", "m4v")),

    QUICKTIME("video/quicktime", arraySetOf("mov")),

    THREEGPP("video/3gpp", arraySetOf("3gp", "3gpp")),

    THREEGPP2("video/3gpp2", arraySetOf("3g2", "3gpp2")),

    MKV("video/x-matroska", arraySetOf("mkv")),

    WEBM("video/webm", arraySetOf("webm")),

    TS("video/mp2ts", arraySetOf("ts")),

    AVI("video/avi", arraySetOf("avi")),
    ;

    private final String mMimeTypeName;
    private final Set<String> mExtensions;

    MimeType(String mimeTypeName, Set<String> extensions) {
        mMimeTypeName = mimeTypeName;
        mExtensions = extensions;
    }

    public static Set<MimeType> ofAll() {
        return EnumSet.allOf(MimeType.class);
    }

    public static Set<MimeType> of(MimeType type, MimeType... rest) {
        return EnumSet.of(type, rest);
    }

    public static Set<MimeType> ofImage() {
        return EnumSet.of(JPEG, PNG, GIF, BMP, WEBP);
    }

    public static Set<MimeType> ofVideo() {
        return EnumSet.of(MPEG, MP4, QUICKTIME, THREEGPP, THREEGPP2, MKV, WEBM, TS, AVI);
    }

    public static boolean isImage(String mimeType) {
        if (mimeType == null) return false;
        return mimeType.startsWith("image");
    }

    public static boolean isImage(Item item) {
        if (item == null) return false;
        return isImage(item.mimeType);
    }

    public static boolean isVideo(String mimeType) {
        if (mimeType == null) return false;
        return mimeType.startsWith("video");
    }

    public static boolean isVideo(Item item) {
        if (item == null) return false;
        return isVideo(item.mimeType);
    }

    public static boolean isGif(String mimeType) {
        if (mimeType == null) return false;
        return mimeType.equals(MimeType.GIF.toString());
    }

    public static boolean isGif(Item item) {
        if (item == null) return false;
        return isGif(item.mimeType);
    }

    private static Set<String> arraySetOf(String... suffixes) {
        return new ArraySet<>(Arrays.asList(suffixes));
    }

    @Override
    public String toString() {
        return mMimeTypeName;
    }

    public boolean checkType(ContentResolver resolver, String path) {
        boolean pathParsed = false;
        for (String extension : mExtensions) {
            if (!pathParsed) {
                // we only resolve the path for one time
                if (!TextUtils.isEmpty(path)) {
                    path = path.toLowerCase(Locale.US);
                }
                pathParsed = true;
            }
            if (path != null && path.endsWith(extension)) {
                return true;
            }
        }
        return false;
    }
}
