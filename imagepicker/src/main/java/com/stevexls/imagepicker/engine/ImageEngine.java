package com.stevexls.imagepicker.engine;

import android.content.Context;
import android.net.Uri;
import android.widget.ImageView;

/**
 * Time：2019/3/27 16:33
 * Description:
 */
public interface ImageEngine {

    void loadThumbnail(Context context, int resize, ImageView imageView, String path);

    void loadThumbnail(Context context, int resize, ImageView imageView, Uri uri);

    void loadGifThumbnail(Context context, int resize, ImageView imageView, String path);

    void loadGifThumbnail(Context context, int resize, ImageView imageView, Uri uri);

    void loadImage(Context context, int resizeX, int resizeY, ImageView imageView, String path);

    void loadImage(Context context, int resizeX, int resizeY, ImageView imageView, Uri uri);

    void loadGifImage(Context context, int resizeX, int resizeY, ImageView imageView, String path);

    void loadGifImage(Context context, int resizeX, int resizeY, ImageView imageView, Uri uri);

    boolean supportAnimatedGif();
}
