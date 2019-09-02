package com.stevexls.imagepicker.ui;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.stevexls.imagepicker.ImagePicker;
import com.stevexls.imagepicker.bean.Album;
import com.stevexls.imagepicker.bean.Item;
import com.stevexls.imagepicker.collection.MediaCollection;
import com.stevexls.imagepicker.collection.MediaPreviewCollection;
import com.stevexls.imagepicker.utils.CollectionUtils;

/**
 * Time：2019/5/9 16:52
 * Description: 图片预览
 * Author:592172833@qq.com
 */
public class MediaPreviewActivity extends BasePreviewActivity implements MediaCollection.MediaCallbacks {

    private MediaPreviewCollection mediaPreviewCollection;

    public static void show(Context context, boolean originalable, Bundle data, Item item, Album album, int requestCode) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ImagePicker.EXTRA_RESULT_ORIGINAL_ENABLE, originalable);
        bundle.putBundle(EXTRA_SELECTION_BUNDLE, data);
        bundle.putParcelable(EXTRA_CLICK_ITEM, item);
        bundle.putParcelable(EXTRA_CURRENT_ALBUM, album);
        Intent intent = new Intent(context, MediaPreviewActivity.class);
        intent.putExtras(bundle);
        ((AppCompatActivity) context).startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Album album = getIntent().getParcelableExtra(EXTRA_CURRENT_ALBUM);
        Item item = getIntent().getParcelableExtra(EXTRA_CLICK_ITEM);
        mediaPreviewCollection = new MediaPreviewCollection();
        mediaPreviewCollection.onCreate(this, this);
        mediaPreviewCollection.loadMedia(album, item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPreviewCollection.onDestroy();
    }

    @Override
    public void onMediaLoad(Cursor cursor) {
        totalItems.clear();
        while (cursor.moveToNext()) {
            Item item = Item.valueOf(cursor);
            totalItems.add(item);
        }
        mediaAdapter.notifyDataSetChanged();
        update();
    }

    private void update() {
        Item item = getIntent().getParcelableExtra(EXTRA_CLICK_ITEM);
        if (item != null && CollectionUtils.isNotEmpty(totalItems)) {
            int currentPos = totalItems.indexOf(item);
            vpContent.setCurrentItem(currentPos, false);

            // 当currentPos==0时，不会触发onPageSelected方法
            if (currentPos == 0) {
                updateHeaderBar(currentPos);
                updateFooterBar(item);
            }
        }
    }

    @Override
    public void onMediaReset() {
        totalItems.clear();
        mediaAdapter.notifyDataSetChanged();
    }
}
