package com.stevexls.imagepicker.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.stevexls.imagepicker.ImagePicker;
import com.stevexls.imagepicker.R;
import com.stevexls.imagepicker.adapter.MediaPreviewAdapter;
import com.stevexls.imagepicker.bean.CustomCause;
import com.stevexls.imagepicker.bean.Item;
import com.stevexls.imagepicker.bean.SelectionSpec;
import com.stevexls.imagepicker.collection.SelectedItemCollection;
import com.stevexls.imagepicker.utils.StatusBarUtils;
import com.stevexls.imagepicker.widget.CheckView;

import java.util.ArrayList;
import java.util.List;

/**
 * Time：2019/6/26 9:52
 * Description:
 */
public class BasePreviewActivity extends AppCompatActivity implements
        ViewPager.OnPageChangeListener,
        View.OnClickListener,
        MediaPreviewFragment.OnViewClickListener {

    public static final String EXTRA_SELECTION_BUNDLE = "extra_selection_bundle";
    public static final String EXTRA_CLICK_ITEM = "extra_click_item";
    public static final String EXTRA_RESULT_APPLY = "extra_result_apply";
    public static final String EXTRA_CURRENT_ALBUM = "extra_current_album";
    public static final String CHECK_STATE = "checkState";

    private View headerBar;
    private View statusBar;
    private ImageView ivBack;
    private Button btnConfirm;
    private TextView tvHeaderDesc;
    protected ViewPager vpContent;
    private RelativeLayout footerBar;
    private LinearLayout llSelect;
    private CheckView checkView;
    private LinearLayout llOriginal;
    private ImageView ivOriginal;
    private int originalOn;
    private int originalOff;

    private boolean isToolbarHide;  // 工具栏是否隐藏
    private boolean originalable;
    protected SelectionSpec mSpec;
    protected SelectedItemCollection selectedItemCollection;
    protected List<Item> totalItems;
    protected MediaPreviewAdapter mediaAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTheme(SelectionSpec.getInstance().themeId);
        super.onCreate(savedInstanceState);

        mSpec = SelectionSpec.getInstance();
        if (!mSpec.hasInited) {
            setResult(RESULT_CANCELED);
            finish();
            return;
        }
        if (mSpec.needOrientationRestriction()) {
            setRequestedOrientation(mSpec.orientation);
        }

        setContentView(R.layout.activity_base_preview);
        initView();
        initData(savedInstanceState);
    }

    private void initView() {
        StatusBarUtils.initStatusBar(this);

        if (mSpec.statusBarDarkMode) {
            StatusBarUtils.setStatusBarDarkMode(this);
        }

        totalItems = new ArrayList<>();
        headerBar = findViewById(R.id.header_bar);
        statusBar = findViewById(R.id.status_bar);
        ivBack = findViewById(R.id.iv_back);
        ivBack.setOnClickListener(this);
        btnConfirm = findViewById(R.id.btn_confirm);
        btnConfirm.setOnClickListener(this);
        tvHeaderDesc = findViewById(R.id.tv_desc);

        vpContent = findViewById(R.id.vp_content);
        mediaAdapter = new MediaPreviewAdapter(getSupportFragmentManager(), totalItems);
        vpContent.addOnPageChangeListener(this);
        vpContent.setAdapter(mediaAdapter);

        footerBar = findViewById(R.id.footer_bar);
        llSelect = findViewById(R.id.ll_select);
        llSelect.setOnClickListener(this);
        checkView = findViewById(R.id.check_view);
        checkView.setCountable(false);
        llOriginal = findViewById(R.id.ll_original);
        llOriginal.setOnClickListener(this);
        ivOriginal = findViewById(R.id.iv_original);
    }

    private void initData(Bundle savedInstanceState) {
        TypedArray ta = getTheme()
            .obtainStyledAttributes(new int[]{
                    R.attr.footerBar_original_image_on,
                    R.attr.footerBar_original_image_off});
        originalOn = ta.getResourceId(0,-1);
        originalOff = ta.getResourceId(1, -1);

        selectedItemCollection = new SelectedItemCollection(this);
        isToolbarHide = false;
        if (savedInstanceState == null) {
            selectedItemCollection.onCreate(getIntent().getBundleExtra(EXTRA_SELECTION_BUNDLE));
            originalable = getIntent().getBooleanExtra(ImagePicker.EXTRA_RESULT_ORIGINAL_ENABLE, false);
        } else {
            selectedItemCollection.onCreate(savedInstanceState);
            originalable = savedInstanceState.getBoolean(CHECK_STATE, false);
        }
        setOriginalEnable(originalable);
        llOriginal.setVisibility(mSpec.originalable ? View.VISIBLE : View.GONE);
    }

    protected void updateHeaderBar(int currentPos) {
        int count = selectedItemCollection.count();
        if (count == 0) {
            btnConfirm.setText(getString(R.string.complete));
            btnConfirm.setEnabled(false);
        } else {
            btnConfirm.setText(getString(R.string.select_complete, count, mSpec.maxSelectable));
            btnConfirm.setEnabled(true);
        }
        tvHeaderDesc.setText(getString(R.string.current_item, currentPos + 1, totalItems.size()));
    }

    protected void updateFooterBar(Item item) {
        if (item == null) {
            return;
        }

        checkView.setChecked(selectedItemCollection.isSelected(item));
        if (item.isVideo()) {
            llOriginal.setVisibility(View.GONE);
        } else if (mSpec.originalable) {
            llOriginal.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onPageScrolled(int i, float v, int i1) {

    }

    @Override
    public void onPageSelected(int i) {
        Item item = totalItems.get(i);
        updateHeaderBar(i);
        updateFooterBar(item);
    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.iv_back) {
            setFinishResult(false);
            finish();
        } else if (id == R.id.btn_confirm) {
            setFinishResult(true);
            finish();
        } else if (id == R.id.ll_select) {
            changeSelect();
        } else if (id == R.id.ll_original) {
            originalable = !originalable;
            setOriginalEnable(originalable);
        }
    }

    private void changeSelect() {
        Item currentItem = totalItems.get(vpContent.getCurrentItem());
        if (selectedItemCollection.isSelected(currentItem)) {
            selectedItemCollection.remove(currentItem);
            checkView.setChecked(false);
        } else {
            if (checkAcceptable(currentItem)) {
                selectedItemCollection.add(currentItem);
                checkView.setChecked(true);
            }
        }
        updateHeaderBar(vpContent.getCurrentItem());
    }

    // 检测是否可以添加
    private boolean checkAcceptable(Item item) {
        CustomCause customCause = selectedItemCollection.isAcceptable(item);
        if (customCause != null) {
            customCause.showCause(this);
            return false;
        }
        return true;
    }

    private void setFinishResult(boolean apply) {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_SELECTION_BUNDLE, selectedItemCollection.getDataWithBundle());
        intent.putExtra(ImagePicker.EXTRA_RESULT_ORIGINAL_ENABLE, originalable);
        intent.putExtra(EXTRA_RESULT_APPLY, apply); // 是否点击了确定按钮
        intent.putExtra(SelectedItemCollection.STATE_COLLECTION_TYPE, selectedItemCollection.getCollectionType());
        setResult(RESULT_OK, intent);
    }

    @Override
    public void onViewClick() {
        if (!isToolbarHide) {
            headerBar.animate().setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    // 隐藏状态栏时间以及图标
                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                            WindowManager.LayoutParams.FLAG_FULLSCREEN);
                    statusBar.setVisibility(View.INVISIBLE);
                    headerBar.setVisibility(View.GONE);
                }
            }).translationYBy(-headerBar.getHeight()).setDuration(200).start();
            footerBar.animate().translationYBy(footerBar.getHeight()).setDuration(200).start();
            isToolbarHide = true;
        } else {
            headerBar.animate().setListener(new AnimatorListenerAdapter() {

                @Override
                public void onAnimationStart(Animator animation) {
                    // 显示状态栏时间以及图标
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                    statusBar.setVisibility(View.VISIBLE);
                    headerBar.setVisibility(View.VISIBLE);
                }
            }).translationYBy(headerBar.getHeight()).setDuration(200).start();
            footerBar.animate().translationYBy(-footerBar.getHeight()).setDuration(200).start();
            isToolbarHide = false;
        }
    }

    @Override
    public void onBackPressed() {
        setFinishResult(false);
        super.onBackPressed();
    }

    private void setOriginalEnable(boolean originalEnable) {
        ivOriginal.setImageResource(originalEnable ? originalOn : originalOff);
    }
}
