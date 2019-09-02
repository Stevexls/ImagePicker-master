package com.stevexls.imagepicker.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

import com.stevexls.imagepicker.R;

/**
 * 选中后半透明浮层
 */
public class MarkImageView extends AppCompatImageView {

    private Paint markPaint;
    private int mPressedMaskColor;
    private boolean isSelected;

    public MarkImageView(Context context) {
        super(context);
        init(context);
    }

    public MarkImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MarkImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        TypedArray ta = context
                .getTheme()
                .obtainStyledAttributes(new int[]{R.attr.item_image_maskColor});
        int defaultColor = ResourcesCompat.getColor(getResources(), R.color.transparent50, getContext().getTheme());
        mPressedMaskColor = ta.getColor(0, defaultColor);
        markPaint = new Paint();
        markPaint.setAntiAlias(true);
        markPaint.setColor(mPressedMaskColor);
        ta.recycle();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (isSelected) {
            canvas.drawRect(0,0,getWidth(), getHeight(),markPaint);
        }
    }

    public void setItemSelected(boolean selected) {
        this.isSelected = selected;
        invalidate();
    }
}
