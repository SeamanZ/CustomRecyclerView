package com.example.customrecyclerview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import java.util.ArrayList;


/**
 * Created by Seaman on 2019-07-12.
 * Banggood Ltd
 */
public class RecommendContentLayout extends LinearLayout {


    protected ArrayList<RecommendChildRecyclerView> mChildViews;
    protected RecommendChildRecyclerView mCurrentView;

    public RecommendContentLayout(Context context) {
        this(context, null);
    }

    public RecommendContentLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RecommendContentLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    //是否已经滑到顶端
    public boolean isScrollToTop() {
        return false;
    }

    public void allChildViewScrollToTop() {

    }

    public void fling(int velocityX, int velocityY) {
        if (this.mCurrentView != null) {
            this.mCurrentView.fling(velocityX, velocityY);
        }
    }

    public void onScroll(int y) {
        if (this.mCurrentView != null) {
            this.mCurrentView.scrollBy(0, y);
        }
    }
}
