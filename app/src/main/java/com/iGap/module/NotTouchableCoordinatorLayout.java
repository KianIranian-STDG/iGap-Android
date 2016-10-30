package com.iGap.module;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * touching on CoordinatorLayout has disabled to only be scrolled while RecyclerView is scrolling
 */
public class NotTouchableCoordinatorLayout extends CoordinatorLayout {
    public NotTouchableCoordinatorLayout(Context context) {
        super(context);
    }

    public NotTouchableCoordinatorLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NotTouchableCoordinatorLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override public boolean onInterceptTouchEvent(MotionEvent ev) {
        return false;
    }
}