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
import com.stevexls.imagepicker.bean.Item;
import com.stevexls.imagepicker.bean.SelectionSpec;

/**
 * Time：2019/6/27 9:47
 * Description:
 */
public class MediaPreviewCollection extends BaseMediaCollection {

    private static final int LOADER_ID = 3;     // 加载所有图片
    private boolean mLoadFinished;

    public void onCreate(AppCompatActivity activity, MediaCallbacks mediaCallbacks) {
        super.onCreate(activity, mediaCallbacks);
        mLoadFinished = false;
    }

    public void loadMedia(Album target, Item item) {
        Bundle args = new Bundle();
        args.putParcelable(ARGS_ALBUM, target);
        args.putParcelable(ARGS_ITEM, item);
        loaderManager.initLoader(LOADER_ID, args, this);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int i, @Nullable Bundle bundle) {
        Context context = mContext.get();
        if (context == null || bundle == null) {
            return null;
        }

        Album album = bundle.getParcelable(ARGS_ALBUM);
        Item item = bundle.getParcelable(ARGS_ITEM);
        if (album == null || item == null) {
            return null;
        }

        String selection;
        String[] selectionArgs;
        if (album.isAll()) {
            if (SelectionSpec.getInstance().onlyShowImages()    // 只显示图片
                    || (SelectionSpec.getInstance().mediaTypeExclusive && item.isImage())) { // 预览时，若不允许同时选择图片和视频,Item为image则只显示图片
                selection = SELECTION_ALL_FOR_SINGLE_MEDIA_TYPE;
                selectionArgs = getSelectionArgsForSingleMediaType(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE);
            } else if (SelectionSpec.getInstance().onlyShowVideos()   // 只显示视频
                    || (SelectionSpec.getInstance().mediaTypeExclusive && item.isVideo())) {  // 预览时，若不允许同时选择图片和视频,Item为video则只显示视频
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
        if (!mLoadFinished) {
            mLoadFinished = true;
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
