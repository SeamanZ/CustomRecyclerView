package com.example.customrecyclerview;

import android.content.Context;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.NestedScrollingParent;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Seaman on 2019-07-12.
 * Banggood Ltd
 */
public class RecommendParentRecyclerView extends RecyclerView implements NestedScrollingParent {
    private AtomicBoolean canScrollVertically;
    private float lastY;
    private FlingHelperUtil mFlingHelper;
    private LinearLayoutManager mLayoutManager;
    private int mMaxDistance;
    private RecommendContentLayout recommendContentLayout;
    private boolean startFling;
    private int totalDy;
    private int velocityY;

    public RecommendParentRecyclerView(@NonNull Context context) {
        this(context, null);
    }


    public RecommendParentRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RecommendParentRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {

        refreshLayoutManager();
        setOverScrollMode(View.OVER_SCROLL_NEVER);
        addOnScrollListener(new InnerOnScrollListener());

        this.canScrollVertically = new AtomicBoolean(true);
        this.mFlingHelper = new FlingHelperUtil(getContext());
        this.mMaxDistance = this.mFlingHelper.getVelocityByDistance(Utils.getScreenHeight(getContext()) * 4D);
    }

    public void refreshLayoutManager() {
        Parcelable parcelable = null;
        if (this.mLayoutManager != null) {
            parcelable = this.mLayoutManager.onSaveInstanceState();
        }

        this.mLayoutManager = new LinearLayoutManager(getContext()) {
            @Override
            public int scrollVerticallyBy(int i, Recycler recycler, State state) {
                try {
                    return super.scrollVerticallyBy(i, recycler, state);
                } catch (Exception e) {
                    e.printStackTrace();
                    return 0;
                }
            }

            @Override
            public void onLayoutChildren(Recycler recycler, State state) {
                try {
                    super.onLayoutChildren(recycler, state);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void addDisappearingView(View view) {
                try {
                    super.addDisappearingView(view);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void addView(View view, int i) {
                try {
                    super.addView(view, i);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public boolean canScrollVertically() {
                final RecommendParentRecyclerView parentRecyclerView = RecommendParentRecyclerView.this;
                final RecommendContentLayout recommendContentLayout = parentRecyclerView.recommendContentLayout;
                return parentRecyclerView.canScrollVertically.get()
                        || recommendContentLayout == null
                        || recommendContentLayout.isScrollToTop();
            }

            @Override
            public boolean supportsPredictiveItemAnimations() {
                return false;
            }

            @Override
            public boolean isAutoMeasureEnabled() {
                return false;
            }
        };

        if (parcelable != null) {
            this.mLayoutManager.onRestoreInstanceState(parcelable);
        }

        this.mLayoutManager.setItemPrefetchEnabled(false);
        this.mLayoutManager.setOrientation(RecyclerView.VERTICAL);
        setLayoutManager(this.mLayoutManager);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        if (ev != null && ev.getAction() == MotionEvent.ACTION_DOWN) {
            this.velocityY = 0;
            stopScroll();
        }

        if (!(ev == null || ev.getActionMasked() == MotionEvent.ACTION_MOVE)) {
            this.lastY = 0.0f;
        }

        try {
            return super.dispatchTouchEvent(ev);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (this.lastY == 0.0f) {
            this.lastY = event.getY();
        }

        if (isScrollToEnd() && this.recommendContentLayout != null) {
            int y = (int) (this.lastY - event.getY());
            this.canScrollVertically.set(false);
            this.recommendContentLayout.onScroll(y);
        }

        if (event.getActionMasked() == MotionEvent.ACTION_UP) {
            this.canScrollVertically.set(true);
        }

        this.lastY = event.getY();

        try {
            return super.onTouchEvent(event);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean onNestedFling(@NonNull View target, float velocityX, float velocityY, boolean consumed) {
        return true;
    }

    @Override
    public boolean onNestedPreFling(@NonNull View target, float velocityX, float velocityY) {
        RecommendContentLayout recommendChild = getRecommendChild();
        boolean z = velocityY > 0.0f && !isScrollToEnd();
        boolean z2;
        if (velocityY >= 0.0f || recommendChild == null || !recommendChild.isScrollToTop()) {
            z2 = false;
        } else {
            z2 = true;
        }
        if (!z && !z2) {
            return false;
        }
        fling(0, (int) velocityY);
        return true;
    }

    @Override
    public boolean onStartNestedScroll(@NonNull View child, @NonNull View target, int nestedScrollAxes) {
        return target instanceof RecommendChildRecyclerView;
    }

    @Override
    public void onNestedPreScroll(@NonNull View target, int dx, int dy, @NonNull int[] consumed) {
        RecommendContentLayout recommendChild = getRecommendChild();
        int i3 = (dy <= 0 || isScrollToEnd()) ? 0 : 1;
        int i4;
        if (dy >= 0 || recommendChild == null || !recommendChild.isScrollToTop()) {
            i4 = 0;
        } else {
            i4 = 1;
        }
        if (i3 != 0 || i4 != 0) {
            scrollBy(0, dy);
            consumed[1] = dy;
        }
    }

    @Override
    public boolean fling(int velocityX, int velocityY) {
        int abs = Math.abs(velocityY);
        if (this.mMaxDistance > 8888 && abs > this.mMaxDistance) {
            velocityY = (abs * this.mMaxDistance) / velocityY;
        }
        boolean fling = super.fling(velocityX, velocityY);
        if (!fling || velocityY <= 0) {
            this.velocityY = 0;
        } else {
            this.startFling = true;
            this.velocityY = velocityY;
        }
        return fling;
    }


    @Nullable
    private RecommendContentLayout getRecommendChild() {
        View childAt = getChildAt(getChildCount() - 1);
        if (childAt instanceof RecommendContentLayout) {
            return (RecommendContentLayout) childAt;
        }
        return null;
    }

    private boolean isScrollToEnd() {
        return !canScrollVertically(1);
    }

    private void dispatchChildFling() {
        if (isScrollToEnd() && this.velocityY != 0) {
            double splineFlingDistance = this.mFlingHelper.getSplineFlingDistance(this.velocityY);
            if (splineFlingDistance > ((double) this.totalDy)) {
                childFling(this.mFlingHelper.getVelocityByDistance(splineFlingDistance - ((double) this.totalDy)));
            }
        }
        this.totalDy = 0;
        this.velocityY = 0;
    }


    public void childFling(int velocityY) {
        if (this.recommendContentLayout != null) {
            this.recommendContentLayout.fling(0, velocityY);
        }
    }

    public void setRecommendContentLayout(RecommendContentLayout recommendContentLayout) {
        this.recommendContentLayout = recommendContentLayout;
    }

    private class InnerOnScrollListener extends OnScrollListener {

        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {

            RecommendParentRecyclerView parentRecyclerView = RecommendParentRecyclerView.this;
            if (parentRecyclerView.startFling) {
                parentRecyclerView.totalDy = 0;
                parentRecyclerView.startFling = false;
            }
            parentRecyclerView.totalDy = parentRecyclerView.totalDy + dy;

            if (!parentRecyclerView.isScrollToEnd()) {
                RecommendContentLayout recommendChild = parentRecyclerView.getRecommendChild();
                if (recommendChild != null) {
                    recommendChild.allChildViewScrollToTop();
                }
            }
        }

        @Override
        public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                RecommendParentRecyclerView.this.dispatchChildFling();
            }
        }
    }
}
