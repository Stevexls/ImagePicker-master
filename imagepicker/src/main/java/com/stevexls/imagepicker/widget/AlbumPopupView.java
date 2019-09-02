package com.stevexls.imagepicker.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.stevexls.imagepicker.R;
import com.stevexls.imagepicker.adapter.AlbumAdapter;
import com.stevexls.imagepicker.bean.Album;

import java.util.ArrayList;
import java.util.List;

/**
 * Time：2019/4/18 18:34
 * Description:相册弹出
 */
public class AlbumPopupView extends RelativeLayout implements View.OnClickListener {

    private RecyclerView rvContent;
    private AlbumAdapter albumAdapter;
    private FrameLayout masker;
    private String TAG = "AlbumPopupView";

    private List<Album> albumList;

    public AlbumPopupView(Context context) {
        super(context);
        init(context);
    }

    public AlbumPopupView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public AlbumPopupView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.layout_pop_album, this, true);
        masker = findViewById(R.id.masker);
        masker.setOnClickListener(this);

        albumList = new ArrayList<>();
        albumAdapter = new AlbumAdapter(context, albumList);

        rvContent = findViewById(R.id.rv_content);
        rvContent.setLayoutManager(new LinearLayoutManager(context));
        rvContent.addItemDecoration(new LinearDecoration(context, LinearDecoration.VERTICAL_LIST, ContextCompat.getDrawable(context, R.drawable.divider_grey2px)));
        rvContent.setAdapter(albumAdapter);
    }

    public void enter() {
        // 设置RecyclerView高度
        int maxHeight = getHeight() * 6 / 8;
        int realHeight = rvContent.getHeight();
        ViewGroup.LayoutParams listParams = rvContent.getLayoutParams();
        listParams.height = realHeight > maxHeight ? maxHeight : realHeight;
        rvContent.setLayoutParams(listParams);

        ObjectAnimator alpha = ObjectAnimator.ofFloat(masker, "alpha", 0, 1);
        ObjectAnimator translationY = ObjectAnimator.ofFloat(rvContent, "translationY", rvContent.getHeight(), 0);
        AnimatorSet set = new AnimatorSet();
        set.setDuration(400);
        set.playTogether(alpha, translationY);
        set.setInterpolator(new AccelerateDecelerateInterpolator());
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                setVisibility(VISIBLE);
            }
        });
        set.start();
    }

    public void exit() {
        ObjectAnimator alpha = ObjectAnimator.ofFloat(masker, "alpha", 1, 0);
        ObjectAnimator translationY = ObjectAnimator.ofFloat(rvContent, "translationY", 0, rvContent.getHeight());
        AnimatorSet set = new AnimatorSet();
        set.setDuration(300);
        set.playTogether(alpha, translationY);
        set.setInterpolator(new AccelerateDecelerateInterpolator());
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                setVisibility(INVISIBLE);
            }
        });
        set.start();
    }

    public void setOnItemClickListener(AlbumAdapter.OnItemClickListener listener) {
        albumAdapter.setOnItemClickListener(listener);
    }

    // 设置选中项
    public void setSelection(int selection) {
        albumAdapter.setSelectIndex(selection);
        albumAdapter.notifyDataSetChanged();
    }

    public void refresh(List<Album> albums) {
        albumList.clear();
        albumList.addAll(albums);
        albumAdapter.notifyDataSetChanged();
    }

    public void refresh(List<Album> albums, int selection) {
        albumList.clear();
        albumList.addAll(albums);
        albumAdapter.setSelectIndex(selection);
        albumAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        exit();
    }
}
