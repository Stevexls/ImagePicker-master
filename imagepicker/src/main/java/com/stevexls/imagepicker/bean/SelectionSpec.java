package com.stevexls.imagepicker.bean;

import android.content.pm.ActivityInfo;
import android.support.annotation.StyleRes;

import com.stevexls.imagepicker.R;
import com.stevexls.imagepicker.engine.ImageEngine;
import com.stevexls.imagepicker.filter.Filter;
import com.stevexls.imagepicker.listener.OnCheckedListener;
import com.stevexls.imagepicker.listener.OnSelectedListener;

import java.util.List;
import java.util.Set;

/**
 * Time：2018/12/20 17:55
 * Description: 参考Matisse: https://github.com/zhihu/Matisse
 */
public class SelectionSpec {

    public Set<MimeType> mimeTypeSet;
    public boolean mediaTypeExclusive;      // 是否可以同时选择图片和视频 false：允许同时选择;    true：不允许同时选择
    public boolean showSingleMediaType;     // 显示单类型文件
    @StyleRes
    public int themeId;                     // 主题
    public int orientation;                 // 屏幕方向
    public boolean countable;               // 是否计数
    public int maxSelectable;               // 最大可选择数
    public int maxImageSelectable;          // Image最大可选择数
    public int maxVideoSelectable;          // Video最大可选择数
    public List<Filter> filters;            // 过滤器集合
    public boolean capture;                 // 是否拍照
    public CaptureStrategy captureStrategy; // 拍照后保存路径
    public int spanCount;                   // 列数
    public float thumbnailScale;            // 缩放倍数
    public ImageEngine imageEngine;         // 加载器
    public boolean hasInited;
    public OnSelectedListener onSelectedListener;   // 点击监听
    public boolean originalable;            // 是否原图
    public boolean autoHideToolbar;         // 自动隐藏toolbar
//    public int originalMaxSize;             // 原始尺寸最大值
    public OnCheckedListener onCheckedListener;     // 是否原图选择监听
    public boolean crop;                    // 是否允许裁剪
    public boolean multiMode;               // 图片的选择模式 true:多选; false:单选
    public boolean showSelected;            // 进入列表页时是否显示已选中的图片 true:显示; false:不显示
    public List<Item> selectedItems;        // 已选中的item
    public int outputWidth = 800;           // 输出宽度
    public int outputHeight = 800;          // 输出高度
    public int focusWidth = 280;            // 焦点框宽度
    public int focusHeight = 280;           // 焦点框高度
    public boolean statusBarDarkMode = false;  // 状态栏字体颜色 true:黑色; false:白色

    private SelectionSpec() {
    }

    public static SelectionSpec getInstance() {
        return InstanceHolder.INSTANCE;
    }

    public static SelectionSpec getCleanInstance() {
        SelectionSpec selectionSpec = getInstance();
        selectionSpec.reset();
        return selectionSpec;
    }

    private void reset() {
        mimeTypeSet = null;
        mediaTypeExclusive = true;
        showSingleMediaType = false;
        themeId = R.style.AppTheme;
        orientation = -1;
        countable = false;
        maxSelectable = 9;
        maxImageSelectable = 0;
        maxVideoSelectable = 0;
        if (filters != null) {
            filters.clear();
            filters = null;
        }
        capture = false;
        captureStrategy = null;
        spanCount = 3;
        thumbnailScale = 0.5f;
//        imageEngine = new GlideEngine();
        hasInited = true;
        originalable = false;
        autoHideToolbar = false;
//        originalMaxSize = Integer.MAX_VALUE;
        onCheckedListener = null;
        crop = false;
        multiMode = false;
        showSelected = false;
        if (selectedItems != null) {
            selectedItems.clear();
            selectedItems = null;
        }
        outputWidth = 800;
        outputHeight = 800;
        focusWidth = 280;
        focusHeight = 280;
        statusBarDarkMode = false;
    }

    public boolean singleSelectionModeEnable() {
        return !countable && (maxSelectable == 1 || (maxImageSelectable == 1 && maxVideoSelectable == 1));
    }

    // 是否需要方向限制(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED：未指定，此为默认值。由Android系统自己选择合适的方向。)
    public boolean needOrientationRestriction() {
        return orientation != ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED;
    }

    public boolean onlyShowImages() {
        return showSingleMediaType && MimeType.ofImage().containsAll(mimeTypeSet);
    }

    public boolean onlyShowVideos() {
        return showSingleMediaType && MimeType.ofVideo().containsAll(mimeTypeSet);
    }

    private static final class InstanceHolder {
        private static final SelectionSpec INSTANCE = new SelectionSpec();
    }
}
