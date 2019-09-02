package com.stevexls.imagepicker.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.stevexls.imagepicker.ImagePicker;
import com.stevexls.imagepicker.R;
import com.stevexls.imagepicker.adapter.AlbumAdapter;
import com.stevexls.imagepicker.adapter.MediaAdapter;
import com.stevexls.imagepicker.bean.Album;
import com.stevexls.imagepicker.bean.Item;
import com.stevexls.imagepicker.bean.SelectionSpec;
import com.stevexls.imagepicker.collection.AlbumCollection;
import com.stevexls.imagepicker.collection.MediaCollection;
import com.stevexls.imagepicker.collection.SelectedItemCollection;
import com.stevexls.imagepicker.utils.CollectionUtils;
import com.stevexls.imagepicker.utils.MediaStoreCompat;
import com.stevexls.imagepicker.utils.StatusBarUtils;
import com.stevexls.imagepicker.widget.AlbumPopupView;
import com.stevexls.imagepicker.widget.CheckView;
import com.stevexls.imagepicker.widget.GridDecoration;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

/**
 * Time：2019/6/16 9:42
 * Description:预览所有的Image
 */
public class ImageGridActivity extends AppCompatActivity implements
        AlbumCollection.AlbumCallbacks,
        MediaCollection.MediaCallbacks,
        MediaAdapter.OnPhotoCaptureListener,
        MediaAdapter.OnMediaGridListener,
        View.OnClickListener,
        EasyPermissions.PermissionCallbacks {

    public static final String CHECK_STATE = "checkState";
    private static final int REQUEST_CODE_CAPTURE = 101;
    private static final int REQUEST_CODE_PREVIEW = 102;
    public static final int REQUEST_PERMISSION_CAMERA = 0x01;

    private SelectedItemCollection mSelectedCollection;     // 选择item相关操作
    private MediaStoreCompat mMediaStoreCompat;             // 调起拍照相关操作类
    private SelectionSpec mSpec;                            // 全局配置项
    private boolean mOriginalEnable;                        // 是否原图

    private ImageView ivBack;               // 头部返回按钮
    private TextView tvDesc;                // 头部描述
    private Button btnConfirm;              // 头部确认按钮
    private RelativeLayout rlDir;           // 图片文件夹点击控件
    private TextView tvDir;                 // 文件夹名称
    private TextView tvPreview;             // 预览按钮
    private ImageView ivOriginal;           // 是否原图
    private LinearLayout llOriginalLayout;  // 原图父布局
    private View emptyView;
    private RecyclerView rvContent;
    private MediaAdapter mediaAdapter;

    private AlbumCollection mAlbumCollection;       // 相册收集
    private AlbumPopupView albumsPopupView;         // 相册View
    private List<Album> mAlbumList;                 // 相册data

    private MediaCollection mMediaCollection;       // 图片收集
    private List<Item> mMediaList;                  // 图片data

    private Album curSelectedAlbum;                 // 当前选中相册
    private int originalOn;
    private int originalOff;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        mSpec = SelectionSpec.getInstance();
        setTheme(mSpec.themeId);      // 设置主题相关
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_picker);

        init(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mSelectedCollection.onSaveInstanceState(outState);
        mAlbumCollection.onSaveInstanceState(outState);
        outState.putBoolean(CHECK_STATE, mOriginalEnable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAlbumCollection.onDestroy();
        mMediaCollection.onDestroy();
    }

    // 一些初始化操作
    private void init(Bundle savedInstanceState) {
        StatusBarUtils.initStatusBar(this);
        if (mSpec.statusBarDarkMode) {
            StatusBarUtils.setStatusBarDarkMode(this); // 设置状态栏字体颜色为黑色
        }

        if (mSpec.needOrientationRestriction()) {   // 屏幕方向
            setRequestedOrientation(mSpec.orientation);
        }

        // 需要拍照的话必须提供 file provider
        if (mSpec.capture) {
            mMediaStoreCompat = new MediaStoreCompat(this);
            if (mSpec.captureStrategy == null) {
                throw new RuntimeException("Don't forget to set CaptureStrategy.");
            }
            mMediaStoreCompat.setCaptureStrategy(mSpec.captureStrategy);
        }

        mSelectedCollection = new SelectedItemCollection(this);
        mSelectedCollection.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mOriginalEnable = savedInstanceState.getBoolean(CHECK_STATE);
        }

        // 是否标记出已选择的图片
        if (mSpec.showSelected
                && CollectionUtils.isNotEmpty(mSpec.selectedItems)
                && savedInstanceState == null) {
            mSelectedCollection.addAll(mSpec.selectedItems);
        }

        initView();
        updateToolbar();
        createAlbumPopupWindow();   // 创建相册弹出窗口
        initData(savedInstanceState);
    }

    private void initView() {
        ivBack = findViewById(R.id.iv_back);
        ivBack.setOnClickListener(this);
        tvDesc = findViewById(R.id.tv_desc);
        btnConfirm = findViewById(R.id.btn_confirm);
        btnConfirm.setOnClickListener(this);
        rlDir = findViewById(R.id.rl_dir);
        rlDir.setOnClickListener(this);
        tvDir = findViewById(R.id.tv_dir);
        tvPreview = findViewById(R.id.tv_preview);
        tvPreview.setOnClickListener(this);
        ivOriginal = findViewById(R.id.iv_original);
        llOriginalLayout = findViewById(R.id.ll_original);
        llOriginalLayout.setOnClickListener(this);
        llOriginalLayout.setVisibility(mSpec.originalable ? View.VISIBLE : View.GONE);
        emptyView = findViewById(R.id.empty_view);
        rvContent = findViewById(R.id.rv_content);
    }

    // 操作栏设置
    private void updateToolbar() {
        int selectedCount = mSelectedCollection.count();    // 选中数
        if (mSpec.multiMode) {
            if (selectedCount == 0) {
                tvPreview.setText(getString(R.string.preview));
                tvPreview.setEnabled(false);
                btnConfirm.setText(getString(R.string.complete));
                btnConfirm.setEnabled(false);
            } else {
                tvPreview.setText(getString(R.string.select_preview, selectedCount));
                tvPreview.setEnabled(true);
                btnConfirm.setText(getString(R.string.select_complete, selectedCount, mSpec.maxSelectable));
                btnConfirm.setEnabled(true);
            }
        } else {
            tvPreview.setVisibility(View.GONE);
            btnConfirm.setVisibility(View.GONE);
        }
        tvDesc.setText(mSpec.onlyShowImages() ? getString(R.string.title_name_photo) :
                (mSpec.onlyShowVideos() ? getString(R.string.title_name_video) : getString(R.string.title_name_all)));
    }

    // 初始化弹出相册列表
    private void createAlbumPopupWindow() {
        albumsPopupView = findViewById(R.id.album_popup_view);
        albumsPopupView.setOnItemClickListener(new AlbumAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                albumsPopupView.setSelection(position);
                albumsPopupView.exit();
                if (mAlbumCollection.getCurrentSelection() != position) {   // 重复点击item不再重新加载图片
                    mAlbumCollection.setCurrentSelection(position);
                    Album album = mAlbumList.get(position);
                    onAlbumSelected(album);
                }
            }
        });
    }

    private void initData(Bundle savedInstanceState) {
        TypedArray ta = getTheme()
                .obtainStyledAttributes(new int[]{
                        R.attr.footerBar_original_image_on,
                        R.attr.footerBar_original_image_off});
        originalOn = ta.getResourceId(0,-1);
        originalOff = ta.getResourceId(1, -1);

        mOriginalEnable = mSpec.originalable;
        setOriginalEnable(mOriginalEnable);
        // 图片加载
        mMediaList = new ArrayList<>();
        mediaAdapter = new MediaAdapter(this, mMediaList, mSelectedCollection, rvContent);
        mediaAdapter.setOnMediaGridListener(this);
        mediaAdapter.setOnPhotoCapture(this);
        rvContent.setLayoutManager(new GridLayoutManager(this, mSpec.spanCount));
        rvContent.setHasFixedSize(true);
        rvContent.addItemDecoration(new GridDecoration(this, ContextCompat.getDrawable(this, R.drawable.grid_divider), mSpec.spanCount));
        rvContent.setAdapter(mediaAdapter);
        mMediaCollection = new MediaCollection();
        mMediaCollection.onCreate(this, this);

        // 相册加载
        mAlbumList = new ArrayList<>();
        mAlbumCollection = new AlbumCollection();
        mAlbumCollection.onCreate(this, this);
        mAlbumCollection.onRestoreInstanceState(savedInstanceState);
        mAlbumCollection.loadAlbums();
    }

    // 相册加载回调
    @Override
    public void onAlbumLoad(Cursor cursor) {
        mAlbumList.clear();
        if(cursor.getCount() == 0) {
            return;
        }
        ArrayList<Album> videoList = new ArrayList<>();
        long totalCount = 0;
        while (cursor.moveToNext()) {
            Album album = Album.valueOf(cursor);
            totalCount = totalCount + album.getCount();
            if (album.getMediaType() == MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO) {
                videoList.add(album);
            } else {
                // 如果相册中有gif跟image，则相册的videoType = MEDIA_TYPE_IMAGE
                mAlbumList.add(album);
            }
        }
        Album albumAll = createAlbumAll(totalCount);
        mAlbumList.add(0, albumAll);
        Album albumVideo = createAlbumVideo(videoList);
        if (albumVideo != null) {
            mAlbumList.add(1, albumVideo);
        }
        albumsPopupView.refresh(mAlbumList, mAlbumCollection.getCurrentSelection());
        onAlbumSelected(mAlbumList.get(mAlbumCollection.getCurrentSelection()));
    }

    // 相册重置
    @Override
    public void onAlbumReset() {
        mAlbumList.clear();
        albumsPopupView.refresh(mAlbumList);
    }

    // 创建所有相册项
    private Album createAlbumAll(long totalCount) {
        String albumAllName = mSpec.onlyShowImages() ? getString(R.string.album_name_photo_all) :
                mSpec.onlyShowVideos() ? getString(R.string.album_name_videos_all) : getString(R.string.album_name_all);
        return new Album(AlbumCollection.ALBUM_ID_ALL,
                mAlbumList.get(0).getCoverPath(),
                albumAllName,
                totalCount,
                AlbumCollection.ALBUM_ALL_MEDIA_TYPE);
    }

    // 创建所有视频相册
    private Album createAlbumVideo(List<Album> videoList) {
        if (videoList == null || videoList.isEmpty()) {
            return null;
        }
        int totalCount = 0;
        for (Album album : videoList) {
            totalCount += album.getCount();
        }
        return new Album(AlbumCollection.ALBUM_ID_VIDEO,
                videoList.get(0).getCoverPath(),
                getString(R.string.album_name_videos_all),
                totalCount,
                MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO);
    }

    // 加载相册内容
    private void onAlbumSelected(Album album) {
        curSelectedAlbum = album;
        // 其他相册，如果里面没有照片的话，不会查询到相册项
        if (album.isAll() && album.isEmpty()) {
            emptyView.setVisibility(View.VISIBLE);
            rvContent.setVisibility(View.GONE);
        } else {
            // 显示具体图片
            emptyView.setVisibility(View.GONE);
            rvContent.setVisibility(View.VISIBLE);
            mMediaCollection.loadMedia(album); // 开始加载
        }
        tvDir.setText(album.getDisplayName());
    }

    // 相册内容加载
    // 按home键再返回此页面时，会触发onLoadFinished方法
    @Override
    public void onMediaLoad(Cursor cursor) {
        mMediaList.clear();
        while (cursor.moveToNext()) {
            Item item = Item.valueOf(cursor);
            Log.i("item",item.toString());
            mMediaList.add(item);
        }
        // 如果是所有相册并且允许拍照
        if (mAlbumList.get(mAlbumCollection.getCurrentSelection()).isAll()
                && mSpec.capture
                && MediaStoreCompat.hasCameraFeature(this)) {
            Item itemCapture = Item.createCaptureItem();
            mMediaList.add(0, itemCapture);
        }
        mediaAdapter.notifyDataSetChanged();
    }

    // 图片加载重置
    @Override
    public void onMediaReset() {
        mMediaList.clear();
        mediaAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.iv_back) {
            finish();
        } else if (id == R.id.btn_confirm) {
            setFinishResult(mSelectedCollection.getList(), mOriginalEnable);
            finish();
        } else if (id == R.id.rl_dir) {
            showAlbumsPopup();  // show albums
        } else if (id == R.id.ll_original) {
            mOriginalEnable = !mOriginalEnable;
            setOriginalEnable(mOriginalEnable);
        } else if (id == R.id.tv_preview) {  // 预览
            SelectedPreviewActivity.show(this, mOriginalEnable, mSelectedCollection.getDataWithBundle(), REQUEST_CODE_PREVIEW);
        }
    }

    // 显示相册列表popup view
    private void showAlbumsPopup() {
        if (albumsPopupView.getVisibility() == View.VISIBLE) {
            albumsPopupView.exit();
        } else {
            albumsPopupView.enter();
        }
    }

    // item 点击
    @Override
    public void onThumbnailClicked(Item item, int itemPosition) {
        if (mSpec.multiMode) {
            Bundle selectedItem = mSelectedCollection.getDataWithBundle();
            MediaPreviewActivity.show(this, mOriginalEnable, selectedItem, item, curSelectedAlbum, REQUEST_CODE_PREVIEW);
        } else {
            mSelectedCollection.add(item);
            setFinishResult(mSelectedCollection.getList(), mOriginalEnable);
            finish();
        }
    }

    // CheckView点击
    @Override
    public void onCheckViewClicked(CheckView checkView, Item item, int position) {
        updateToolbar();
        if (mSpec.onSelectedListener != null) {
            mSpec.onSelectedListener.onSelected(mSelectedCollection.getList());
        }
    }

    // 点击拍摄图片回调
    @Override
    public void capture() {
        if (EasyPermissions.hasPermissions(this, Manifest.permission.CAMERA)) {
            startCapture();
        } else {
            EasyPermissions.requestPermissions(this, "need camera permission", REQUEST_PERMISSION_CAMERA, Manifest.permission.CAMERA);
        }
    }

    private void startCapture() {
        mMediaStoreCompat.dispatchCaptureIntent(this, REQUEST_CODE_CAPTURE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    // 权限申请同意
    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        if (requestCode == REQUEST_PERMISSION_CAMERA) {
            startCapture();
        }
    }

    // 权限申请拒绝
    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        Toast.makeText(this, getString(R.string.permission_request_denied), Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }

        if (requestCode == REQUEST_CODE_CAPTURE) { // 拍摄图片返回
            onCaptureResult();
        } else if (requestCode == REQUEST_CODE_PREVIEW) { // 预览界面返回
            onPreviewResult(data);
        }
    }

    // 拍摄返回
    private void onCaptureResult() {
        // 发送广播刷新
        MediaStoreCompat.sendToRefreshPic(this, new File(mMediaStoreCompat.getCurrentPhotoPath()));
        // 根据返回的图片地址获取详细的Item信息 延时获取避免获取到Item对象为null
        new DelayHandler(this).postDelayed(new Runnable() {
            @Override
            public void run() {
                Item captureItem = mSelectedCollection.getCurrentPhotoItem(mMediaStoreCompat.getCurrentPhotoPath());
                mSelectedCollection.add(captureItem);
                if (mSpec.crop) { // 如果需要裁剪，跳转到裁剪界面
                    // TODO 新版本再添加裁剪功能

                } else { // 不需要裁剪
                    setFinishResult(mSelectedCollection.getList(), mOriginalEnable);
                    // 移除Uri权限
                    mMediaStoreCompat.revokePermission(ImageGridActivity.this, mMediaStoreCompat.getCurrentPhotoUri(), Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    finish();
                }
            }
        },400);
    }

    // 预览返回
    private void onPreviewResult(@Nullable Intent data) {
        Bundle bundle = data.getBundleExtra(BasePreviewActivity.EXTRA_SELECTION_BUNDLE);
        ArrayList<Item> selected = bundle.getParcelableArrayList(SelectedItemCollection.STATE_SELECTION);
        mOriginalEnable = data.getBooleanExtra(ImagePicker.EXTRA_RESULT_ORIGINAL_ENABLE, false);
        setOriginalEnable(mOriginalEnable);
        int collectionType = bundle.getInt(SelectedItemCollection.STATE_COLLECTION_TYPE, SelectedItemCollection.COLLECTION_UNDEFINED);
        boolean apply = data.getBooleanExtra(BasePreviewActivity.EXTRA_RESULT_APPLY, false);
        if (apply) {
            setFinishResult(selected, mOriginalEnable);
            finish();
        } else {
            mSelectedCollection.overwrite(selected, collectionType);
            mediaAdapter.notifyDataSetChanged();
            updateToolbar();
        }
    }

    private void setFinishResult(ArrayList<Item> resultItem, boolean originalEnable) {
        Intent intent = new Intent();
        intent.putParcelableArrayListExtra(ImagePicker.EXTRA_RESULT_SELECTION, resultItem);
        intent.putExtra(ImagePicker.EXTRA_RESULT_ORIGINAL_ENABLE, originalEnable);
        setResult(RESULT_OK, intent);
    }

    private void setOriginalEnable(boolean originalEnable) {
        ivOriginal.setImageResource(originalEnable ? originalOn : originalOff);
    }

    private static class DelayHandler extends Handler {
        private WeakReference<Activity> mActivity;

        private DelayHandler(Activity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {

        }
    }
}
