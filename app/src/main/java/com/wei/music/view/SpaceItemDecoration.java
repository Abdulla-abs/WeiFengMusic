package com.wei.music.view;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by xiangcheng on 17/8/22.
 */

public class SpaceItemDecoration extends RecyclerView.ItemDecoration {

    private int top;
    private int left;
    private int right;
    private int bottom;

    public SpaceItemDecoration(int space) {
        this.top = space;
        this.left = space;
        this.right = space;
        this.bottom = space;
    }

    public SpaceItemDecoration(int top, int left, int right, int bottom) {
        this.top = top;
        this.left = left;
        this.right = right;
        this.bottom = bottom;
    }

    public SpaceItemDecoration(int topBottom, int leftRight) {
        this.top = topBottom;
        this.left = leftRight;
        this.right = leftRight;
        this.bottom = topBottom;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, RecyclerView parent, @NonNull RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view);
        int itemCount;

        RecyclerView.Adapter adapter = parent.getAdapter();
        if (adapter == null){
            itemCount = 0;
        }else {
            itemCount = adapter.getItemCount();
        }

        outRect.top = top;
        outRect.left = position == 0 ? 0 : left;  // 第一个item left=0
        outRect.right = position == itemCount - 1 ? 0 : right;  // 最后一个item right=0
        outRect.bottom = bottom;
    }


}