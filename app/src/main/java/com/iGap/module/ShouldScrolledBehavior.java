package com.iGap.module;

import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.MotionEvent;
import android.view.View;

import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;


/**
 * scroll when adapter's items are not completely visible. in other words,
 * toolbar is always visible till adapter's items are enough to be scrolled.
 */
public class ShouldScrolledBehavior extends AppBarLayout.ScrollingViewBehavior {
    private LinearLayoutManager mLayoutManager;
    private FastItemAdapter mAdapter;

    public ShouldScrolledBehavior(LinearLayoutManager layoutManager, FastItemAdapter adapter) {
        super();
        this.mLayoutManager = layoutManager;
        this.mAdapter = adapter;
    }

    @Override
    public boolean onInterceptTouchEvent(CoordinatorLayout parent, View child, MotionEvent ev) {
        return shouldScrolled();
    }

    public boolean shouldScrolled() {
        // adapter has more items that not shown yet
        if (mLayoutManager.findLastCompletelyVisibleItemPosition()
                != mAdapter.getAdapterItemCount() - 1) {
            return true;
        }
        // last completely visible item is the last item in adapter but it may be occurred in 2 ways:
        // 1) all items are shown
        // 2) scrolled to the last item (implemented following)
        else if (mLayoutManager.findLastCompletelyVisibleItemPosition()
                == mAdapter.getAdapterItemCount() - 1
                && mLayoutManager.findFirstCompletelyVisibleItemPosition() != 0) {
            return true;
        }
        return false;
    }
}