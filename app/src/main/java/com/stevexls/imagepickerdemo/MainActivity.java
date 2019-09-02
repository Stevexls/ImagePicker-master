package com.stevexls.imagepickerdemo;

import android.Manifest;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.stevexls.imagepicker.ImagePicker;
import com.stevexls.imagepicker.bean.CaptureStrategy;
import com.stevexls.imagepicker.bean.Item;
import com.stevexls.imagepicker.bean.MimeType;
import com.stevexls.imagepicker.widget.GridDecoration;

import java.util.ArrayList;
import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

import static com.stevexls.imagepicker.ImagePicker.EXTRA_RESULT_SELECTION;

public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks, ImageSelectAdapter.OnImageClickListener {

    private String TAG = "MainActivity";

    private RecyclerView rvContent;
    private List<Item> selectItems;
    private ImageSelectAdapter mAdapter;

    public static final int REQUEST_PERMISSION_STORAGE = 0x01;
    public static final int REQUEST_CODE_CHOOSE = 0x03;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        selectItems = new ArrayList<>();
        mAdapter = new ImageSelectAdapter(this, selectItems, 9);
        mAdapter.setOnImageClickListener(this);
        rvContent = findViewById(R.id.rv_content);
        rvContent.setAdapter(mAdapter);
        rvContent.setLayoutManager(new GridLayoutManager(this, 4));
        rvContent.addItemDecoration(new GridDecoration(this, ContextCompat.getDrawable(this, R.drawable.grid_divider_white), 4));
    }

    private void enterImagePicker() {
        ImagePicker.from(this)
                .choose(MimeType.ofAll(), false)
                .showSingleMediaType(false)
                .countable(true)        // 是否允许计数
                .theme(R.style.Custom_Theme)
                .multiMode(true)       // 图片的选择模式 true:多选; false:单选
                .maxSelectable(8)       // 最大选择数
                .imageEngine(new GlideEngine())     // 图片加载器
                .captureStrategy(
                        new CaptureStrategy(true, "com.stevexls.imagepickerdemo.fileprovider", "ImagePicker/Pictures")) // 前两个参数指定拍摄后保存基础路径为/mnt/sdcard,第三个参数指定自定义目的文件夹/mnt/sdcard/ImagePicker/Pictures
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)  // 竖屏
                .capture(true)      // 允许拍摄
//                    .thumbnailScale(1.0f)
                .spanCount(4)               // 列数
                .statucBarDarkMode(true)    // 状态栏颜色
                .originalEnable(true)   // 原图
                .showSelected(true)     // 标记已选择的图片，参数为true时必须传selectedItems
                .selectedItems(selectItems)     // 已选中的图片列表
                .forResult(REQUEST_CODE_CHOOSE);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        enterImagePicker();
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        Toast.makeText(this, "permission denied", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onImageClick(int totalCount, Item item, int position) {
        if (item.getId() == -1) {
            if (EasyPermissions.hasPermissions(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                enterImagePicker();
            } else {
                EasyPermissions.requestPermissions(this, "need permission", REQUEST_PERMISSION_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
        } else {

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK || data == null) {
            return;
        }
        ArrayList<Item> mLists = data.getParcelableArrayListExtra(EXTRA_RESULT_SELECTION);
        selectItems.clear();
        selectItems.addAll(mLists);
        mAdapter.refreshData(selectItems);
    }
}
