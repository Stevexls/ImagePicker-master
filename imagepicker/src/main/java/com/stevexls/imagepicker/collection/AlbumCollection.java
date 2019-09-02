package com.stevexls.imagepicker.collection;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;

import com.stevexls.imagepicker.bean.SelectionSpec;

import java.lang.ref.WeakReference;

/**
 * Time：2019/1/23 16:32
 * Description:相册查询
 */
public class AlbumCollection implements LoaderManager.LoaderCallbacks<Cursor> {

    private String TAG = "AlbumCollection";

    private static final int LOADER_ALL = 1;     // 加载所有图片
    private static final String STATE_CURRENT_SELECTION = "state_current_selection";
    private WeakReference<Context> mContext;
    private LoaderManager loaderManager;
    private AlbumCallbacks albumCallbacks;
    private int mCurrentSelection = 0;      // 当前选中相册, 默认0
    private boolean mLoadFinished;      // 是否加载完成
    public static final String ALBUM_ID_ALL = "-1";
    public static final String ALBUM_ID_VIDEO = "-2";
    public static final int ALBUM_ALL_MEDIA_TYPE = -1;

    public static final String COLUMN_COUNT = "count";
    private Uri QUERY_URI = MediaStore.Files.getContentUri("external");
    private String[] PROJECTION = {
            MediaStore.Files.FileColumns._ID,
            MediaStore.Images.Media.BUCKET_ID,      // 相册id
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME,    // 相册名称
            MediaStore.Images.Media.DATA,           // 图片的真实路径  /storage/emulated/0/pp/downloader/wallpaper/aaa.jpg
            MediaStore.Files.FileColumns.MEDIA_TYPE, // 类型
            "COUNT(*) AS " + COLUMN_COUNT        // 相册包含图片数量
    };

    // 过滤(图片或视频)
    // 真实sql = where(SELECTION),所以此处 GROUP BY会加上括号
    // 同一个bucketId, mediaType不同，会产生两条数据
    private String SELECTION = "(" + MediaStore.Files.FileColumns.MEDIA_TYPE + "=?"
            + " OR "
            + MediaStore.Files.FileColumns.MEDIA_TYPE + "=?)"
            + " AND " + MediaStore.MediaColumns.SIZE + ">0"
            + ") GROUP BY (" + MediaStore.Images.Media.BUCKET_ID + "), (media_type";

    // 过滤(单个类型)
    private String SELECTION_FOR_SINGLE_MEDIA_TYPE = MediaStore.Files.FileColumns.MEDIA_TYPE + "=?"
            + "AND " + MediaStore.MediaColumns.SIZE + ">0"
            + ") GROUP BY (" + MediaStore.Images.Media.BUCKET_ID;

    // 图片或视频
    private String[] SELECTION_ARGS = {
            String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE),
            String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO),
    };

    // 时间倒序
    private String BUCKET_ORDER_BY = "datetaken DESC";

    public void onCreate(AppCompatActivity activity, AlbumCallbacks albumCallbacks) {
        mContext = new WeakReference<Context>(activity);
        this.albumCallbacks = albumCallbacks;
        loaderManager = activity.getSupportLoaderManager();
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int i, @Nullable Bundle bundle) {
        Context context = mContext.get();
        if (context == null) {
            return null;
        }
        mLoadFinished = false;
        String selection;
        String[] selectionArgs;
        if (SelectionSpec.getInstance().onlyShowImages()) {
            // 只显示图片
            selection = SELECTION_FOR_SINGLE_MEDIA_TYPE;
            selectionArgs = getSelectionArgsForSingleMediaType(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE);
        } else if (SelectionSpec.getInstance().onlyShowVideos()) {
            // 只显示视频
            selection = SELECTION_FOR_SINGLE_MEDIA_TYPE;
            selectionArgs = getSelectionArgsForSingleMediaType(MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO);
        } else {
            // 显示图片与视频
            selection = SELECTION;
            selectionArgs = SELECTION_ARGS;
        }
        // (上下文对象；要获取的内容的URL；要返回的列，传null返回所有列；过滤器；过滤器的值；排序行为)
        return new CursorLoader(context, QUERY_URI, PROJECTION, selection, selectionArgs, BUCKET_ORDER_BY);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        if (mContext.get() == null) {
            return;
        }
        if (!mLoadFinished) {
            mLoadFinished = true;
            albumCallbacks.onAlbumLoad(cursor);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        if (mContext.get() == null) {
            return;
        }

        albumCallbacks.onAlbumReset();
    }

    // 转换成字符串数组
    private String[] getSelectionArgsForSingleMediaType(int mediaType) {
        return new String[]{String.valueOf(mediaType)};
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            return;
        }
        mCurrentSelection = savedInstanceState.getInt(STATE_CURRENT_SELECTION);
    }

    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(STATE_CURRENT_SELECTION, mCurrentSelection);
    }

    public void onDestroy() {
        if (loaderManager != null) {
            loaderManager.destroyLoader(LOADER_ALL);
        }
        albumCallbacks = null;
    }

    public void loadAlbums() {
        Bundle bundle = new Bundle();
        loaderManager.initLoader(LOADER_ALL, bundle, this);
    }

    // 获取当前选中相册
    public int getCurrentSelection() {
        return mCurrentSelection;
    }

    // 设置当前选中相册
    public void setCurrentSelection(int currentSelection) {
        mCurrentSelection = currentSelection;
    }

    public interface AlbumCallbacks {
        // 这里直接把查询后的游标返回，由使用方去构建所需数据格式
        void onAlbumLoad(Cursor cursor);

        void onAlbumReset();
    }
}
