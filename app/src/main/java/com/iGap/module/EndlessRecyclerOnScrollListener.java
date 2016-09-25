package com.iGap.module;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.util.Log;
import android.view.View;

import com.iGap.realm.RealmRoomMessage;
import com.mikepenz.fastadapter.adapters.FastItemAdapter;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class EndlessRecyclerOnScrollListener extends RecyclerViewPauseOnScrollListener {
    private int mPreviousTotal = 0;
    private boolean mLoading = true;
    private int mCurrentPage = 1;
    private FastItemAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    private int mTotalItemsInPages = 100;
    private int mVisibleThreshold = -1;
    private OrientationHelper mOrientationHelper;
    private boolean mIsOrientationHelperVertical;
    private int mFirstVisibleItem, mVisibleItemCount, mTotalItemCount;
    private List<RealmRoomMessage> mMessagesList = new ArrayList<>();

    public EndlessRecyclerOnScrollListener(List<RealmRoomMessage> messageList, FastItemAdapter adapter, ImageLoader imageLoader, boolean pauseOnScroll, boolean pauseOnSettling) {
        super(imageLoader, pauseOnScroll, pauseOnSettling);
        mAdapter = adapter;
        mMessagesList = messageList;
    }

    public EndlessRecyclerOnScrollListener(List<RealmRoomMessage> messageList, FastItemAdapter adapter, ImageLoader imageLoader, boolean pauseOnScroll, boolean pauseOnSettling, OnScrollListener customListener) {
        super(imageLoader, pauseOnScroll, pauseOnSettling, customListener);
        mAdapter = adapter;
        mMessagesList = messageList;
    }


    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        if (this.mLayoutManager == null) {
            this.mLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        }

        int footerItemCount = mAdapter != null ? mAdapter.getAdapterItemCount() : 0;

        if (mVisibleThreshold == -1) {
            mVisibleThreshold = findLastVisibleItemPosition(recyclerView) - findFirstVisibleItemPosition(recyclerView) - footerItemCount;
        }

        mVisibleItemCount = recyclerView.getChildCount() - footerItemCount;
        mTotalItemCount = mLayoutManager.getItemCount() - footerItemCount;
        mFirstVisibleItem = findFirstVisibleItemPosition(recyclerView);

        mTotalItemCount = mAdapter.getItemCount();

        if (mLoading) {
            if (mTotalItemCount > mPreviousTotal) {
                mLoading = false;
                mPreviousTotal = mTotalItemCount;
            }
        }

        if (!mLoading && mLayoutManager.findFirstVisibleItemPosition() - mVisibleThreshold <= 0) {
            // reached top
            Log.i(EndlessRecyclerOnScrollListener.class.getSimpleName(), "reaching top");
            mCurrentPage++;

            onLoadMore(this, mCurrentPage);

            mLoading = true;
        }
    }

    private int findLastVisibleItemPosition(RecyclerView recyclerView) {
        final View child = findOneVisibleChild(recyclerView.getChildCount() - 1, -1, false, true);
        return child == null ? RecyclerView.NO_POSITION : recyclerView.getChildAdapterPosition(child);
    }

    private int findFirstVisibleItemPosition(RecyclerView recyclerView) {
        final View child = findOneVisibleChild(0, mLayoutManager.getChildCount(), false, true);
        return child == null ? RecyclerView.NO_POSITION : recyclerView.getChildAdapterPosition(child);
    }

    public abstract void onLoadMore(EndlessRecyclerOnScrollListener listener, int page);

    public List<RealmRoomMessage> loadMore(int page) {
        int startFrom = page * mTotalItemsInPages;
        List<RealmRoomMessage> messages = new ArrayList<>();

        // +addSettingMessagesCount because of not counting add setting messages
        for (int i = startFrom, addSettingMessagesCount = 0; i < (startFrom) + mTotalItemsInPages + addSettingMessagesCount; i++) {
            if (i < mMessagesList.size()) {
                messages.add(mMessagesList.get(i));
                if (mMessagesList.get(i).isOnlyTime()) {
                    addSettingMessagesCount++;
                }
            } else {
                break;
            }
        }

        Collections.sort(messages, SortMessages.ASC);

        return messages;
    }

    private View findOneVisibleChild(int fromIndex, int toIndex, boolean completelyVisible,
                                     boolean acceptPartiallyVisible) {
        if (mLayoutManager.canScrollVertically() != mIsOrientationHelperVertical
                || mOrientationHelper == null) {
            mIsOrientationHelperVertical = mLayoutManager.canScrollVertically();
            mOrientationHelper = mIsOrientationHelperVertical
                    ? OrientationHelper.createVerticalHelper(mLayoutManager)
                    : OrientationHelper.createHorizontalHelper(mLayoutManager);
        }

        final int start = mOrientationHelper.getStartAfterPadding();
        final int end = mOrientationHelper.getEndAfterPadding();
        final int next = toIndex > fromIndex ? 1 : -1;
        View partiallyVisible = null;
        for (int i = fromIndex; i != toIndex; i += next) {
            final View child = mLayoutManager.getChildAt(i);
            if (child != null) {
                final int childStart = mOrientationHelper.getDecoratedStart(child);
                final int childEnd = mOrientationHelper.getDecoratedEnd(child);
                if (childStart < end && childEnd > start) {
                    if (completelyVisible) {
                        if (childStart >= start && childEnd <= end) {
                            return child;
                        } else if (acceptPartiallyVisible && partiallyVisible == null) {
                            partiallyVisible = child;
                        }
                    } else {
                        return child;
                    }
                }
            }
        }
        return partiallyVisible;
    }

    public void resetPageCount() {
        this.resetPageCount(0);
    }

    public void resetPageCount(int page) {
        this.mPreviousTotal = 0;
        this.mLoading = true;
        this.mCurrentPage = page;
        this.onLoadMore(this, this.mCurrentPage);
    }

    public RecyclerView.LayoutManager getLayoutManager() {
        return mLayoutManager;
    }

    public int getTotalItemCount() {
        return mTotalItemCount;
    }

    public int getFirstVisibleItem() {
        return mFirstVisibleItem;
    }

    public int getVisibleItemCount() {
        return mVisibleItemCount;
    }

    public int getCurrentPage() {
        return mCurrentPage;
    }
}
