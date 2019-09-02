package com.stevexls.imagepickerdemo;

import android.content.Context;
import android.net.Uri;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.stevexls.imagepicker.R;
import com.stevexls.imagepicker.engine.ImageEngine;

public class GlideEngine implements ImageEngine {

    @Override
    public void loadThumbnail(Context context, int resize, ImageView imageView, String path) {
        Glide.with(context)
                .asDrawable()
                .load(path)
                .apply(new RequestOptions()
                        .override(resize, resize)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .fitCenter())
                .into(imageView);
    }

    @Override
    public void loadThumbnail(Context context, int resize, ImageView imageView, Uri uri) {
        Glide.with(context)
                .asDrawable()
                .load(uri)
                .apply(new RequestOptions()
                        .override(resize, resize)
                        .fitCenter())
                .into(imageView);
    }

    @Override
    public void loadGifThumbnail(Context context, int resize, ImageView imageView, String path) {
        Glide.with(context)
                .asBitmap()
                .load(path)
                .apply(new RequestOptions()
                        .override(resize, resize)
                        .fitCenter())
                .into(imageView);
    }

    @Override
    public void loadGifThumbnail(Context context, int resize, ImageView imageView, Uri uri) {
        Glide.with(context)
                .asBitmap()
                .load(uri)
                .apply(new RequestOptions()
                        .override(resize, resize)
                        .fitCenter())
                .into(imageView);
    }


    @Override
    public void loadImage(Context context, int resizeX, int resizeY, ImageView imageView, String path) {
        Glide.with(context)
                .load(path)
                .apply(new RequestOptions()
                        .override(resizeX, resizeY)
                        .priority(Priority.HIGH)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .fitCenter())
                .into(imageView);
    }

    @Override
    public void loadImage(Context context, int resizeX, int resizeY, ImageView imageView, Uri uri) {
        Glide.with(context)
                .load(uri)
                .apply(new RequestOptions()
                        .override(resizeX, resizeY)
                        .priority(Priority.HIGH)
                        .fitCenter())
                .into(imageView);
    }

    @Override
    public void loadGifImage(Context context, int resizeX, int resizeY, ImageView imageView, String path) {
        Glide.with(context)
                .asGif()
                .load(path)
                .apply(new RequestOptions()
                        .override(resizeX, resizeY)
                        .priority(Priority.HIGH)
                        .fitCenter())
                .into(imageView);
    }

    @Override
    public void loadGifImage(Context context, int resizeX, int resizeY, ImageView imageView, Uri uri) {
        Glide.with(context)
                .asGif()
                .load(uri)
                .apply(new RequestOptions()
                        .override(resizeX, resizeY)
                        .priority(Priority.HIGH)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .fitCenter())
                .into(imageView);
    }

    @Override
    public boolean supportAnimatedGif() {
        return true;
    }

}
