package com.stevexls.imagepicker.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.stevexls.imagepicker.ImagePicker;

/**
 * Time：2019/7/22 15:31
 * Description:
 */
public class SelectedPreviewActivity extends BasePreviewActivity{

    public static void show(Context context, boolean originalable, Bundle data, int requestCode) {
        Intent intent = new Intent(context, SelectedPreviewActivity.class);
        intent.putExtra(ImagePicker.EXTRA_RESULT_ORIGINAL_ENABLE, originalable);
        intent.putExtra(EXTRA_SELECTION_BUNDLE, data);
        ((AppCompatActivity) context).startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        totalItems.addAll(selectedItemCollection.getList());
        mediaAdapter.notifyDataSetChanged();
        updateHeaderBar(0);
        updateFooterBar(totalItems.get(0));
    }
}
