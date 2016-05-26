package com.mirrorchannelth.internship.adapter;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by boss on 5/26/16.
 */
public class GalleryItemDecoration extends RecyclerView.ItemDecoration {

    private int space;

    public GalleryItemDecoration(int space) {
        this.space = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view,
                               RecyclerView parent, RecyclerView.State state) {
        outRect.left = space;
        outRect.right = space;
        outRect.bottom = space;
        int position = parent.getChildAdapterPosition(view);
    }
}
