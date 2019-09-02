package com.stevexls.imagepicker.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.os.EnvironmentCompat;

import com.stevexls.imagepicker.R;
import com.stevexls.imagepicker.bean.CaptureStrategy;

import java.io.File;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Time：2019/4/1 11:22
 * Description: 拍照类
 */
public class MediaStoreCompat {

    private String TAG = "MediaStoreCompat";

    private WeakReference<Activity> mContext;
    private WeakReference<Fragment> mFragment;
    private CaptureStrategy mCaptureStrategy;
    private Uri mCurrentPhotoUri;
    private String mCurrentPhotoPath;

    public MediaStoreCompat(Activity activity) {
        mContext = new WeakReference<>(activity);
        mFragment = null;
    }

    public MediaStoreCompat(Activity activity, Fragment fragment) {
        mContext = new WeakReference<>(activity);
        mFragment = new WeakReference<>(fragment);
    }

    /**
     * Checks whether the device has a camera feature or not.
     * 判断是否存在camera模块
     *
     * @param context a context to check for camera feature.
     * @return true if the device has a camera feature. false otherwise.
     */
    public static boolean hasCameraFeature(Context context) {
        PackageManager packageManager = context.getApplicationContext().getPackageManager();
        return packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    // 发送广播刷新图片
    public static void sendToRefreshPic(Context context, File file) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        // 是否会报错？7.0以上兼容问题
        // Uri.fromFile本身并不会报错,若版本大于7.0时,使用Uri.fromFile获取的Uri去startActivity,则会报错,sendBroadcast却没问题.
        Uri contentUri = Uri.fromFile(file);
        mediaScanIntent.setData(contentUri);
        context.sendBroadcast(mediaScanIntent);
    }

    // 发送广播刷新图片
    public static void sendToRefreshPic(Context context, Uri uri) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(uri);
        context.sendBroadcast(mediaScanIntent);
    }

    public void setCaptureStrategy(CaptureStrategy strategy) {
        mCaptureStrategy = strategy;
    }

    public void dispatchCaptureIntent(Context context, int requestCode) {
        Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        captureIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        if (captureIntent.resolveActivity(context.getPackageManager()) != null) {    // 检查是否有对应的Activity响应这个intent
            File photoFile = createImageFile();

            if (photoFile != null) {
                // 默认情况下，即不需要指定intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                // 照相机有自己默认的存储路径，拍摄的照片将返回一个缩略图。如果想访问原始图片，
                // 可以通过data extra能够得到原始图片位置。即，如果指定了目标uri，data就没有数据，
                // 如果没有指定uri，则data就返回有数据！

                mCurrentPhotoPath = photoFile.getAbsolutePath(); // 绝对路径

                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
                    mCurrentPhotoUri = Uri.fromFile(photoFile);
                } else {
                    // URI访问
                    mCurrentPhotoUri = ImagePickerProvider.getUriForFile(mContext.get(), mCaptureStrategy.authority, photoFile);
                    captureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    // 加入uri权限 要不三星手机不能拍照
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                        List<ResolveInfo> resInfoList = context.getPackageManager().queryIntentActivities(captureIntent, PackageManager.MATCH_DEFAULT_ONLY);
                        for (ResolveInfo resolveInfo : resInfoList) {
                            String packageName = resolveInfo.activityInfo.packageName;
                            context.grantUriPermission(packageName, mCurrentPhotoUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        }
                    }
                }
                // 设置了MediaStore.EXTRA_OUTPUT后，onActivityResult中返回的Intent为空
                captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mCurrentPhotoUri);  // 设置保存图片的路径
                if (mFragment != null) {
                    mFragment.get().startActivityForResult(captureIntent, requestCode);
                } else {
                    mContext.get().startActivityForResult(captureIntent, requestCode);
                }
            } else {
                ToastUtils.showToastShort(mContext.get(), mContext.get().getString(R.string.sdcard_error));
            }
        }
    }

    private File createImageFile() {
        // create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = String.format("JPEG_%s.jpg", timeStamp);
        File storageDir;
        if (mCaptureStrategy.isPublic) {    // 任意路径 /mnt/sdcard
            storageDir = Environment.getExternalStorageDirectory();
            if (!storageDir.exists()) {
                storageDir.mkdirs();
            }
        } else {    // 本安装文件目录下 /mnt/sdcard/Android/data/package_name/files/Pictures
            storageDir = mContext.get().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        }
        if (mCaptureStrategy.directory != null) {   // 自定义文件路径
            storageDir = new File(storageDir, mCaptureStrategy.directory);
            if (!storageDir.exists()) {
                storageDir.mkdirs();
            }
        }

        File tempFile = new File(storageDir, imageFileName);

        // 判断SD卡是否正常挂载
        if (!Environment.MEDIA_MOUNTED.equals(EnvironmentCompat.getStorageState(tempFile))) {
            return null;
        }
        return tempFile;
    }

    public Uri getCurrentPhotoUri() {
        return mCurrentPhotoUri;
    }

    public String getCurrentPhotoPath() {
        return mCurrentPhotoPath;
    }

    public void revokePermission(Context context, Uri uri, int modeFlags) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            context.revokeUriPermission(uri, modeFlags);
        }
    }
}
