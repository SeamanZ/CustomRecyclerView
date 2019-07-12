package com.example.customrecyclerview;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import java.util.concurrent.atomic.AtomicBoolean;


/**
 * Created by Seaman on 2019-07-12.
 * Banggood Ltd
 */
public class RecommendChildRecyclerView extends RecyclerView {

    private FlingHelperUtil mFlingHelper;
    private int mMaxDistance;
    private RecyclerView parentRecyclerView;

    private AtomicBoolean isDestroy = new AtomicBoolean(false);
    private AtomicBoolean isScrollTop = new AtomicBoolean(false);

    private boolean startFling;
    private int velocityY;
    private int totalDy;

    private RecommendChildRecyclerView(@NonNull Context context) {
        super(context);
    }

    public RecommendChildRecyclerView(@NonNull Context context, @NonNull RecyclerView parentRecyclerView) {
        this(context);
        this.parentRecyclerView = parentRecyclerView;

        setOverScrollMode(View.OVER_SCROLL_NEVER);
        addOnScrollListener(new InnerOnScrollListener());

        this.mFlingHelper = new FlingHelperUtil(getContext());
        this.mMaxDistance = this.mFlingHelper.getVelocityByDistance((double) (Utils.getScreenHeight(context) * 4));


    }

    private void dispatchParentFling() {
        if (this.parentRecyclerView != null) {
            if (isScrollToTop() && this.velocityY != 0) {
                double splineFlingDistance = this.mFlingHelper.getSplineFlingDistance(this.velocityY);
                if (splineFlingDistance > ((double) Math.abs(this.totalDy))) {
                    int velocityY = this.mFlingHelper.getVelocityByDistance(
                            splineFlingDistance + ((double) this.totalDy));
                    this.parentRecyclerView.fling(0, -velocityY);
                }
            }
            this.totalDy = 0;
            this.velocityY = 0;
        }
    }

    @Override
    public boolean fling(int velocityX, int velocityY) {

        if (!isAttachedToWindow()) {
            return false;
        }

        int abs = Math.abs(velocityY);
        if (this.mMaxDistance > 8888 && abs > this.mMaxDistance) {
            velocityY = (abs * this.mMaxDistance) / velocityY;
        }

        boolean fling = super.fling(velocityX, velocityY);
        if (!fling || velocityY >= 0) {
            this.velocityY = 0;
        } else {
            this.startFling = true;
            this.velocityY = velocityY;
        }

        return fling;
    }

    public void scrollToTop() {
        this.isScrollTop.set(true);
        if (!isScrollToTop()) {
            try {
                scrollToPosition(0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public boolean isScrollToTop() {
        return !canScrollVertically(-1);
    }


    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event != null && event.getAction() == 0) {
            this.velocityY = 0;
        }
        return super.dispatchTouchEvent(event);
    }


    private void notifyLayoutChange() {
        LayoutManager layoutManager = getLayoutManager();
        if (layoutManager instanceof StaggeredGridLayoutManager) {
            ((StaggeredGridLayoutManager) layoutManager).invalidateSpanAssignments();
        }
    }

    private int getLastVisibleItem() {
        LayoutManager layoutManager = getLayoutManager();
        if ((layoutManager instanceof StaggeredGridLayoutManager)) {
            int[] into = ((StaggeredGridLayoutManager) layoutManager).findLastVisibleItemPositions(null);
            return into[0] > into[into.length - 1] ? into[0] : into[into.length - 1];
        } else if (!(layoutManager instanceof GridLayoutManager)) {
            return -1;
        } else {
            return ((GridLayoutManager) layoutManager).findLastVisibleItemPosition();
        }
    }


    private int getFirstVisibleItem() {
        LayoutManager layoutManager = getLayoutManager();
        if ((layoutManager instanceof StaggeredGridLayoutManager)) {
            int[] into = ((StaggeredGridLayoutManager) layoutManager).findFirstVisibleItemPositions(null);
            return into[0] > into[into.length - 1] ? into[into.length - 1] : into[0];
        } else if (!(layoutManager instanceof GridLayoutManager)) {
            return -1;
        } else {
            return ((GridLayoutManager) layoutManager).findFirstVisibleItemPosition();
        }
    }

    public boolean isLastCompleteVisible() {
        try {
            if (this.parentRecyclerView != null) {
                LayoutManager layoutManager = this.parentRecyclerView.getLayoutManager();
                if (layoutManager instanceof LinearLayoutManager) {
                    int lastCompletelyVisibleItemPosition = ((LinearLayoutManager) layoutManager)
                            .findLastCompletelyVisibleItemPosition();
                    return lastCompletelyVisibleItemPosition >= layoutManager.getItemCount() - 1;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    public int getTotalDy() {
        return totalDy;
    }

    public void onPageSelected(int i) {
        this.isDestroy.set(false);
    }

    private class InnerOnScrollListener extends OnScrollListener {

        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {

            RecommendChildRecyclerView childRecyclerView = RecommendChildRecyclerView.this;
            if (!childRecyclerView.isDestroy.get()) {
                if (childRecyclerView.startFling) {
                    childRecyclerView.totalDy = 0;
                    childRecyclerView.startFling = false;
                }
                childRecyclerView.totalDy = childRecyclerView.totalDy + dy;
            }
        }

        @Override
        public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
            RecommendChildRecyclerView childRecyclerView = RecommendChildRecyclerView.this;
            if (!childRecyclerView.isDestroy.get()) {

                if (childRecyclerView.getFirstVisibleItem() == 0) {
                    childRecyclerView.notifyLayoutChange();
                }

                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    childRecyclerView.dispatchParentFling();
                }

            }

        }
    }
}
