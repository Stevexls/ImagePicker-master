package com.stevexls.imagepicker.collection;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;

import com.stevexls.imagepicker.bean.Album;
import com.stevexls.imagepicker.bean.SelectionSpec;

/**
 * Time：2019/4/29 11:31
 * Description: 图片收集
 */
public class MediaCollection extends BaseMediaCollection {

    private static final int LOADER_ID = 2;     // 加载所有图片
    private boolean isLoadFinished;

    public void onCreate(AppCompatActivity activity, MediaCallbacks mediaCallbacks) {
        super.onCreate(activity, mediaCallbacks);
        isLoadFinished = false;
    }

    public void loadMedia(Album target) {
        isLoadFinished = false;
        Bundle args = new Bundle();
        args.putParcelable(ARGS_ALBUM, target);
        // 如果加载器已经存在，则重启；否则创建一个新的
        if (loaderManager.getLoader(LOADER_ID) != null) {
            loaderManager.restartLoader(LOADER_ID, args, this);
        } else {
            loaderManager.initLoader(LOADER_ID, args, this);
        }
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int i, @Nullable Bundle bundle) {
        Context context = mContext.get();
        if (context == null || bundle == null) {
            return null;
        }

        Album album = bundle.getParcelable(ARGS_ALBUM);
        if (album == null) {
            return null;
        }

        String selection;
        String[] selectionArgs;
        if (album.isAll()) {    // 如果显示所有图片
            if (SelectionSpec.getInstance().onlyShowImages()) { // 只显示图片
                selection = SELECTION_ALL_FOR_SINGLE_MEDIA_TYPE;
                selectionArgs = getSelectionArgsForSingleMediaType(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE);
            } else if (SelectionSpec.getInstance().onlyShowVideos()) { // 只显示视频
                selection = SELECTION_ALL_FOR_SINGLE_MEDIA_TYPE;
                selectionArgs = getSelectionArgsForSingleMediaType(MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO);
            } else { // 显示图片和视频
                selection = SELECTION_ALL;
                selectionArgs = SELECTION_ALL_ARGS;
            }
        } else if (album.isVideo()) { // 显示所有视频
            selection = SELECTION_ALL_FOR_SINGLE_MEDIA_TYPE;
            selectionArgs = getSelectionArgsForSingleMediaType(MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO);
        } else {    // 显示所有图片
            selection = SELECTION_ALBUM_FOR_SINGLE_MEDIA_TYPE;
            selectionArgs = getSelectionAlbumArgsForSingleMediaType(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE,
                    album.getBucketId());
        }
        return new CursorLoader(context, QUERY_URI, PROJECTION, selection, selectionArgs, ORDER_BY);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        if (mContext.get() == null) {
            return;
        }
        if (!isLoadFinished) {
            isLoadFinished = true;
            mediaCallbacks.onMediaLoad(cursor);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        if (mContext.get() == null) {
            return;
        }
        mediaCallbacks.onMediaReset();
    }

    public void onDestroy() {
        if (loaderManager != null) {
            loaderManager.destroyLoader(LOADER_ID);
        }
        mediaCallbacks = null;
    }
}
