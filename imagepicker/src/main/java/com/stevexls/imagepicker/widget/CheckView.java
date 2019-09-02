package com.stevexls.imagepicker.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.content.res.ResourcesCompat;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.stevexls.imagepicker.R;
import com.stevexls.imagepicker.utils.DensityUtils;

/**
 * Time：2019/5/17 14:13
 * Description: 选择框(计数/直接选中)
 */
public class CheckView extends View {

    private String TAG = "CheckView";

    private int rectMargin;    // 边框margin dp
    private int borderWidth;    // 边框大小

    private boolean mCountable;  // 是否计数
    private boolean mChecked;   // 是否选中
    private int mCountNum;    // 计数值

    private int borderColor = Color.parseColor("#ffffff");    // 边框颜色
    private int backgroundColor = Color.parseColor("#1AAD19");     // 背景颜色
    private int textColor = Color.parseColor("#ffffff");      // 文字颜色
    private RectF rectF;    // 边框矩形
    private Paint strokePaint;  // 边框画笔
    private Paint backgroundPaint;  // 选中时背景色画笔
    private TextPaint textPaint;    // 文字画笔
    private Drawable mCheckDrawable;    // 选中图标

    private boolean mEnable = true;

    public CheckView(Context context) {
        super(context);
        init(context);
    }

    public CheckView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CheckView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        rectMargin = DensityUtils.dp2px(context, 7);
        borderWidth = DensityUtils.dp2px(context, 1);
        rectF = new RectF();

        TypedArray ta = getContext()
                .getTheme()
                .obtainStyledAttributes(new int[]{R.attr.item_check_borderColor,
                        R.attr.item_check_backgroundColor,
                        R.attr.item_check_image,
                        R.attr.item_check_textColor});
        borderColor = ta.getColor(0, borderColor);
        backgroundColor = ta.getColor(1, backgroundColor);
        mCheckDrawable = ta.getDrawable(2);
        if (mCheckDrawable == null) {
            mCheckDrawable = ResourcesCompat.getDrawable(context.getResources(), R.drawable.ic_vector_check, context.getTheme());
        }
        textColor = ta.getColor(3, textColor);
        ta.recycle();
        initStrokePaint(context);
    }

    private void initStrokePaint(Context context) {
        strokePaint = new Paint();
        strokePaint.setAntiAlias(true);
        strokePaint.setStyle(Paint.Style.STROKE);
        strokePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));
        strokePaint.setStrokeWidth(borderWidth);
        strokePaint.setColor(borderColor);
    }

    private void initBackgroundPaint() {
        if (backgroundPaint == null) {
            backgroundPaint = new Paint();
            backgroundPaint.setAntiAlias(true);
            backgroundPaint.setStyle(Paint.Style.FILL);
            backgroundPaint.setColor(backgroundColor);
        }
    }

    private void initTextPaint() {
        if (textPaint == null) {
            textPaint = new TextPaint();
            textPaint.setAntiAlias(true);
            textPaint.setColor(Color.WHITE);
            textPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
            textPaint.setTextSize(DensityUtils.dp2px(getContext(), 12));
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        rectF.left = rectF.top = rectMargin;
        rectF.right = getWidth() - rectMargin;
        rectF.bottom = getHeight() - rectMargin;
        canvas.drawRoundRect(rectF, 5, 5, strokePaint);

        if (mCountable) {
            if (mCountNum > 0) {
                initTextPaint();
                rectF.setEmpty();
                rectF.left = rectF.top = rectMargin - borderWidth;
                rectF.right = getWidth() - rectMargin + borderWidth;
                rectF.bottom = getHeight() - rectMargin + borderWidth;
                initBackgroundPaint();
                canvas.drawRoundRect(rectF, 6, 6, backgroundPaint);
                String num = String.valueOf(mCountNum);
                int baseX = (int) ((getWidth() - textPaint.measureText(num)) / 2);
                int baseY = (int) ((getHeight() - textPaint.descent() - textPaint.ascent()) / 2);
                canvas.drawText(num, baseX, baseY, textPaint);
            }
        } else {
            if (mChecked) {
                rectF.setEmpty();
                rectF.left = rectF.top = rectMargin - borderWidth;
                rectF.right = getWidth() - rectMargin + borderWidth;
                rectF.bottom = getHeight() - rectMargin + borderWidth;
                initBackgroundPaint();
                canvas.drawRoundRect(rectF, 6, 6, backgroundPaint);
                mCheckDrawable.setBounds(rectMargin, rectMargin, getWidth() - rectMargin, getHeight() - rectMargin);
                mCheckDrawable.draw(canvas);
            }
        }
    }

    public void setChecked(boolean checked) {
        if (mCountable) {
            // 计数模式下不支持选中
            Log.e(TAG, "CheckView is countable, call setCheckedNum(int) instead.");
            return;
        }
        mChecked = checked;
        invalidate();
    }

    public void setCountable(boolean countable) {
        mCountable = countable;
    }

    public void setCountNum(int countNum) {
        if (!mCountable) {
            Log.e(TAG, "CheckView is not countable, call setChecked() instead.");
            return;
        }
        if (countNum <= 0) {
            Log.e(TAG, "checked num can't be negative.");
            return;
        }
        mCountNum = countNum;
        invalidate();
    }

    public void reset() {
        mCountable = false;
        mCountNum = -1;
        mChecked = false;
        invalidate();
    }

    public void setEnabled(boolean enabled) {
        if (mEnable != enabled) {
            mEnable = enabled;
            invalidate();
        }
    }
}
