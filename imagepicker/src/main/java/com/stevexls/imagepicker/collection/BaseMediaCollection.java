package com.stevexls.imagepicker.collection;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.LoaderManager;
import android.support.v7.app.AppCompatActivity;

import java.lang.ref.WeakReference;

/**
 * Time：2019/6/28 10:10
 * Description: 图片查询基类
 */
public abstract class BaseMediaCollection implements LoaderManager.LoaderCallbacks<Cursor>{

    protected final String ARGS_ALBUM = "args_album";
    protected final String ARGS_ITEM = "args_item";

    protected WeakReference<Context> mContext;
    protected MediaCallbacks mediaCallbacks;
    protected LoaderManager loaderManager;
    protected Uri QUERY_URI = MediaStore.Files.getContentUri("external");

    // 选中列
    protected String[] PROJECTION = {
            MediaStore.Files.FileColumns._ID,
            MediaStore.MediaColumns.DISPLAY_NAME,
            MediaStore.Images.Media.DATA,           // 图片的真实路径  /storage/emulated/0/pp/downloader/wallpaper/aaa.jpg
            MediaStore.Images.Media.MIME_TYPE,      // 图片的类型     image/jpeg
            MediaStore.Images.Media.SIZE,           // 图片的大小，long型  132492
            "duration",
            MediaStore.Images.Media.DATE_ADDED,
            MediaStore.Images.Media.WIDTH,          //图片的宽度，int型  1920
            MediaStore.Images.Media.HEIGHT,         //图片的高度，int型  1080
    };

    // 过滤(不指定相册，多个类型)
    protected final String SELECTION_ALL =
            "(" + MediaStore.Files.FileColumns.MEDIA_TYPE + "=?"
                    + " OR "
                    + MediaStore.Files.FileColumns.MEDIA_TYPE + "=?)"
                    + " AND " + MediaStore.MediaColumns.SIZE + ">0";
    protected final String[] SELECTION_ALL_ARGS = {
            String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE),
            String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO),
    };

    // 过滤(不指定相册，单个类型)
    protected final String SELECTION_ALL_FOR_SINGLE_MEDIA_TYPE =
            MediaStore.Files.FileColumns.MEDIA_TYPE + "=?"
                    + " AND " + MediaStore.MediaColumns.SIZE + ">0";

    protected String[] getSelectionArgsForSingleMediaType(int mediaType) {
        return new String[]{String.valueOf(mediaType)};
    }

    // 过滤(指定相册，单个类型)
    protected final String SELECTION_ALBUM_FOR_SINGLE_MEDIA_TYPE =
            MediaStore.Files.FileColumns.MEDIA_TYPE + "=?"
                    + " AND "
                    + " bucket_id=?"
                    + " AND " + MediaStore.MediaColumns.SIZE + ">0";

    protected String[] getSelectionAlbumArgsForSingleMediaType(int mediaType, String albumId) {
        return new String[]{String.valueOf(mediaType), albumId};
    }

    protected final String ORDER_BY = MediaStore.Images.Media.DATE_TAKEN + " DESC";

    public void onCreate(AppCompatActivity activity, MediaCallbacks mediaCallbacks) {
        mContext = new WeakReference<Context>(activity);
        this.mediaCallbacks = mediaCallbacks;
        loaderManager = activity.getSupportLoaderManager();
    }

    public interface MediaCallbacks {
        void onMediaLoad(Cursor cursor);

        void onMediaReset();
    }
}
