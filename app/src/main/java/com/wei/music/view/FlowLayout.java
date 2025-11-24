package com.wei.music.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;

public class FlowLayout extends ViewGroup {
    private float mVerticalSpacing; //每个item纵向间距
    private float mHorizontalSpacing; //每个item横向间距

    public FlowLayout(Context context) {
        super(context);
    }
    public FlowLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public void setHorizontalSpacing(float pixelSize) {
        mHorizontalSpacing = pixelSize;
    }
    public void setVerticalSpacing(float pixelSize) {
        mVerticalSpacing = pixelSize;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int selfWidth = resolveSize(0, widthMeasureSpec);

        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int paddingRight = getPaddingRight();
        int paddingBottom = getPaddingBottom();

        int childLeft = paddingLeft;
        int childTop = paddingTop;
        int lineHeight = 0;

        // 新增：记录当前行是否已经有item了
        boolean isFirstInLine = true;

        for (int i = 0, childCount = getChildCount(); i < childCount; ++i) {
            View childView = getChildAt(i);
            if (childView.getVisibility() == View.GONE) {
                continue;
            }

            LayoutParams childLayoutParams = childView.getLayoutParams();
            childView.measure(
                    getChildMeasureSpec(widthMeasureSpec, paddingLeft + paddingRight, childLayoutParams.width),
                    getChildMeasureSpec(heightMeasureSpec, paddingTop + paddingBottom, childLayoutParams.height));

            int childWidth = childView.getMeasuredWidth();
            int childHeight = childView.getMeasuredHeight();

            lineHeight = Math.max(childHeight, lineHeight);

            // 判断是否需要换行
            if (!isFirstInLine && childLeft + childWidth + paddingRight > selfWidth) {
                // 换行
                childLeft = paddingLeft;
                childTop += mVerticalSpacing + lineHeight;
                lineHeight = childHeight;
                isFirstInLine = true;  // 新行第一个
            }

            // 如果不是当前行的第一个，才加上横向间距
            if (!isFirstInLine) {
                childLeft += mHorizontalSpacing;
            }

            isFirstInLine = false;  // 标记这行已经有item了
        }

        int wantedHeight = childTop + lineHeight + paddingBottom;
        setMeasuredDimension(selfWidth, resolveSize(wantedHeight, heightMeasureSpec));
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int myWidth = r - l;

        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int paddingRight = getPaddingRight();

        int childLeft = paddingLeft;
        int childTop = paddingTop;
        int lineHeight = 0;

        boolean isFirstInLine = true;  // 同样加上这个标志

        for (int i = 0, childCount = getChildCount(); i < childCount; ++i) {
            View childView = getChildAt(i);
            if (childView.getVisibility() == View.GONE) {
                continue;
            }

            int childWidth = childView.getMeasuredWidth();
            int childHeight = childView.getMeasuredHeight();

            lineHeight = Math.max(childHeight, lineHeight);

            // 换行判断
            if (!isFirstInLine && childLeft + childWidth + paddingRight > myWidth) {
                childLeft = paddingLeft;
                childTop += mVerticalSpacing + lineHeight;
                lineHeight = childHeight;
                isFirstInLine = true;
            }

            // 关键修复：只有不是本行第一个时才加横向间距
            if (!isFirstInLine) {
                childLeft += mHorizontalSpacing;
            }

            childView.layout(childLeft, childTop, childLeft + childWidth, childTop + childHeight);

            childLeft += childWidth;  // 移动到下一个位置（不直接加 spacing，由上面控制）
            isFirstInLine = false;
        }
    }
}
