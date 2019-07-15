package com.example.customrecyclerview;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.NestedScrollingParent2;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.ref.WeakReference;

/**
 * Created by Seaman on 2019-07-15.
 * Banggood Ltd
 */
public class NestedRecyclerView2 extends RecyclerView implements NestedScrollingParent2 {

    private WeakReference<View> currentShowNestedScrollTarget; //当前用户看到的嵌套滑动子视图
    private View nestedScrollTarget; //用户手动划动的子视图
    private boolean nestedScrollTargetIsBeingDragged;
    private boolean nestedScrollTargetWasUnableToScroll;
    private boolean skipsTouchInterception;

    public NestedRecyclerView2(@NonNull Context context) {
        this(context, null);
    }

    public NestedRecyclerView2(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NestedRecyclerView2(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        boolean temporarilySkipsInterception = nestedScrollTarget != null;
        if (temporarilySkipsInterception) {
            // If a descendent view is scrolling we set a flag to temporarily skip our onInterceptTouchEvent implementation
            skipsTouchInterception = true;
        }

        // First dispatch, potentially skipping our onInterceptTouchEvent
        boolean handled = super.dispatchTouchEvent(ev);

        if (temporarilySkipsInterception) {
            skipsTouchInterception = false;

            // If the first dispatch yielded no result or we noticed that the descendent view is unable to scroll in the
            // direction the user is scrolling, we dispatch once more but without skipping our onInterceptTouchEvent.
            // Note that RecyclerView automatically cancels active touches of all its descendents once it starts scrolling
            // so we don't have to do that.
            if (!handled || nestedScrollTargetWasUnableToScroll) {
                handled = super.dispatchTouchEvent(ev);
            }
        }

        return handled;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        if (isScrollToBottom() && currentShowNestedScrollTarget != null) {
            //当前显示的嵌套滑动子视图还可以向上划动就不拦截事件
            View view = currentShowNestedScrollTarget.get();
            if (view != null && view.canScrollVertically(-1)) {
                return false;
            }
        }

        return !skipsTouchInterception && super.onInterceptTouchEvent(e);
    }

    //判断当前RecyclerView是否已经划到内容底部
    private boolean isScrollToBottom() {
        return !canScrollVertically(1);
    }


    @Override
    public boolean onNestedPreFling(@NonNull View target, float velocityX, float velocityY) {
        Log.v("NestedRecyclerView2", "onNestedPreFling isScrollToBottom = " + isScrollToBottom() + " velocityY = " + velocityY);
        if (velocityY > 0 && !isScrollToBottom()) {
            return super.fling(0, (int) velocityY);
        } else {
            return false;
        }
    }


    @Override
    public boolean onStartNestedScroll(@NonNull View child, @NonNull View target, int axes, int type) {
        return (axes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0;
    }

    @Override
    public void onNestedScrollAccepted(@NonNull View child, @NonNull View target, int axes, int type) {
        if ((axes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0) {
            // A descendent started scrolling, so we'll observe it.
            currentShowNestedScrollTarget = new WeakReference<>(target);
            nestedScrollTarget = target;
            nestedScrollTargetIsBeingDragged = false;
            nestedScrollTargetWasUnableToScroll = false;
        }
    }

    @Override
    public void onStopNestedScroll(@NonNull View target, int type) {
        // The descendent finished scrolling. Clean up!
        nestedScrollTarget = null;
        nestedScrollTargetIsBeingDragged = false;
        nestedScrollTargetWasUnableToScroll = false;
    }

    @Override
    public void onNestedScroll(@NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type) {

        if (target == nestedScrollTarget && !nestedScrollTargetIsBeingDragged) {
            //dyConsumed != 0 说明子视图在滚动
            if (dyConsumed != 0) {
                // The descendent was actually scrolled, so we won't bother it any longer.
                // It will receive all future events until it finished scrolling.
                nestedScrollTargetIsBeingDragged = true;
                nestedScrollTargetWasUnableToScroll = false;
            } else if (dyUnconsumed != 0) { //dyUnconsumed != 0 说明子视图已经不能滚动
                // The descendent tried scrolling in response to touch movements but was not able to do so.
                // We remember that in order to allow RecyclerView to take over scrolling.
                nestedScrollTargetWasUnableToScroll = true;
                ViewParent parent = target.getParent();
                if (parent != null) {
                    parent.requestDisallowInterceptTouchEvent(false);
                }
            }
        }

    }

    @Override
    public void onNestedPreScroll(@NonNull View target, int dx, int dy, @NonNull int[] consumed, int type) {
        Log.v("NestedRecyclerView2", "onNestedPreScroll isScrollToBottom = " + isScrollToBottom()
                + " dy = " + dy + " target = " + target.getClass().getSimpleName());
        //dy>0 说明手指是向上划动，如果RecyclerView还没划到底部先把自己划到底部
        if (dy > 0 && !isScrollToBottom()) {
            scrollBy(dx, dy);
            consumed[1] = dy;
        }
    }


}
