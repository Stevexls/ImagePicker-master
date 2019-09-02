package com.stevexls.imagepickerdemo;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.stevexls.imagepicker.engine.ImageEngine;
import com.stevexls.imagepicker.utils.ImagePickerProvider;

import java.io.File;

/**
 * Time：2019/8/2 13:34
 * Description:
 */
public class PicassoEngine implements ImageEngine {
    @Override
    public void loadThumbnail(Context context, int resize, ImageView imageView, String path) {
        Picasso.get()
                .load("file://" + path)
                .resize(resize, resize)
                .into(imageView);
    }

    @Override
    public void loadThumbnail(Context context, int resize, ImageView imageView, Uri uri) {
        Picasso.get()
                .load(uri)
                .resize(resize, resize)
                .into(imageView);
    }

    @Override
    public void loadGifThumbnail(Context context, int resize, ImageView imageView, String path) {
        Picasso.get()
                .load("file://" + path)
                .resize(resize, resize)
                .into(imageView);
    }

    @Override
    public void loadGifThumbnail(Context context, int resize, ImageView imageView, Uri uri) {
        Picasso.get()
                .load(uri)
                .resize(resize, resize)
                .into(imageView);
    }

    @Override
    public void loadImage(Context context, int resizeX, int resizeY, ImageView imageView, String path) {
        Picasso.get()
                .load("file://" + path)
                .resize(resizeX, resizeY)
                .centerInside()
                .priority(Picasso.Priority.HIGH)
                .into(imageView);
    }

    @Override
    public void loadImage(Context context, int resizeX, int resizeY, ImageView imageView, Uri uri) {
        Picasso.get()
                .load(uri)
                .resize(resizeX, resizeY)
                .centerInside()
                .priority(Picasso.Priority.HIGH)
                .into(imageView);
    }

    @Override
    public void loadGifImage(Context context, int resizeX, int resizeY, ImageView imageView, String path) {
        Picasso.get()
                .load("file://" + path)
                .resize(resizeX, resizeY)
                .centerInside()
                .priority(Picasso.Priority.HIGH)
                .into(imageView);
    }

    @Override
    public void loadGifImage(Context context, int resizeX, int resizeY, ImageView imageView, Uri uri) {
        Picasso.get()
                .load(uri)
                .resize(resizeX, resizeY)
                .centerInside()
                .priority(Picasso.Priority.HIGH)
                .into(imageView);
    }

    @Override
    public boolean supportAnimatedGif() {
        return false;
    }
}
