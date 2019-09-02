package com.stevexls.imagepicker.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;


/**
 * GridView分割线
 */

public class GridDecoration extends RecyclerView.ItemDecoration {

    private Context context;
    private Drawable mDivider;
    private int spanCount;
    private int spacing;

    public GridDecoration(Context context) {

    }

    public GridDecoration(Context context, Drawable drawable, int spanCount) {
        this.context = context;
        this.mDivider = drawable;
        this.spanCount = spanCount;
        this.spacing = mDivider.getIntrinsicWidth();
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        drawVertical(c, parent);
        drawHorizontal(c, parent);
    }

    private void drawHorizontal(Canvas c, RecyclerView parent) {
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            int index = parent.getChildAdapterPosition(child); // item position
            if (index >= spanCount) {
                final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
                int column = i % spanCount;      // item所在列
                int leftMargin = column * spacing / spanCount;
                int rightMargin = spacing - (column + 1) * spacing / spanCount;

                int left = child.getLeft() - leftMargin - params.leftMargin;
                int right = child.getRight() + rightMargin + params.rightMargin;
                int bottom = child.getTop() + params.topMargin;
                int top = bottom - mDivider.getIntrinsicWidth();

                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(c);
            }

        }
    }

    private void drawVertical(Canvas c, RecyclerView parent) {
        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            int column = i % spanCount;      // item所在列
            View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            int leftMargin = column * spacing / spanCount;
            int rightMargin = spacing - (column + 1) * spacing / spanCount;

            // itemView左边需要绘制的分割线宽度
            int leftL = child.getLeft() - leftMargin - params.leftMargin;
            int leftR = leftL + leftMargin;

            // itemView右边需要绘制的分割线宽度
            int rightL = child.getRight() + params.rightMargin;
            int rightR = rightL + rightMargin;

            // 分割线的上、下边界
            int top = child.getTop() - params.topMargin;
            int bottom = child.getBottom() + params.bottomMargin;

            mDivider.setBounds(leftL, top, leftR, bottom);
            mDivider.draw(c);

            mDivider.setBounds(rightL, top, rightR, bottom);
            mDivider.draw(c);
        }
    }

    // DividerGridItemDecoration 把divider的width设置大些。recyclerview设为 >=2 列，就发现左边的item加上divider的宽度等于右边的item的宽度，两个item宽度不一致
    // 此处已解决上述问题
    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int itemPosition = parent.getChildAdapterPosition(view); // item position
        int column = itemPosition % spanCount;      // item所在列
        outRect.left = column * spacing / spanCount;
        outRect.right = spacing - (column + 1) * spacing / spanCount;
        if (itemPosition >= spanCount) {
            outRect.top = spacing;
        }
    }
}
