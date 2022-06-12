package net.iGap.messenger.ui.components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.util.SparseArray;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import net.iGap.libs.emojiKeyboard.View.CubicBezierInterpolator;
import net.iGap.module.customView.RecyclerListView;

import java.util.ArrayList;
import java.util.HashMap;

public class RecyclerAnimationScrollHelper {

    public final static int SCROLL_DIRECTION_UNSET = -1;
    public final static int SCROLL_DIRECTION_DOWN = 0;
    public final static int SCROLL_DIRECTION_UP = 1;

    private final RecyclerListView recyclerView;
    private final LinearLayoutManager layoutManager;
    private final HashMap<Long, View> oldStableIds = new HashMap<>();
    public SparseArray<View> positionToOldView = new SparseArray<>();
    private int scrollDirection;
    private ValueAnimator animator;
    private ScrollListener scrollListener;
    private AnimationCallback animationCallback;

    public RecyclerAnimationScrollHelper(RecyclerListView recyclerView, LinearLayoutManager layoutManager) {
        this.recyclerView = recyclerView;
        this.layoutManager = layoutManager;
    }

    public void scrollToPosition(int position, int offset) {
        scrollToPosition(position, offset, layoutManager.getReverseLayout(), false);
    }

    public void scrollToPosition(int position, int offset, boolean bottom) {
        scrollToPosition(position, offset, bottom, false);
    }

    public void scrollToPosition(int position, int offset, final boolean bottom, boolean smooth) {
        if (recyclerView.fastScrollAnimationRunning || (recyclerView.getItemAnimator() != null && recyclerView.getItemAnimator().isRunning())) {
            return;
        }
        if (!smooth || scrollDirection == SCROLL_DIRECTION_UNSET) {
            layoutManager.scrollToPositionWithOffset(position, offset);
            return;
        }

        boolean scrollDown = scrollDirection == SCROLL_DIRECTION_DOWN;

        recyclerView.setScrollEnabled(false);

        final ArrayList<View> oldViews = new ArrayList<>();
        positionToOldView.clear();

        RecyclerView.Adapter adapter = recyclerView.getAdapter();
        oldStableIds.clear();
        int n = recyclerView.getChildCount();
        for (int i = 0; i < n; i++) {
            View child = recyclerView.getChildAt(i);
            oldViews.add(child);
            int childPosition = layoutManager.getPosition(child);
            positionToOldView.put(childPosition, child);
            if (adapter != null && adapter.hasStableIds()) {
                long itemId = ((RecyclerView.LayoutParams) child.getLayoutParams()).getViewPosition();
                oldStableIds.put(itemId, child);
            }
        }

        AnimatableAdapter animatableAdapter = null;
        if (adapter instanceof AnimatableAdapter) {
            animatableAdapter = (AnimatableAdapter) adapter;
        }

        layoutManager.scrollToPositionWithOffset(position, offset);
        if (adapter != null) adapter.notifyDataSetChanged();
        AnimatableAdapter finalAnimatableAdapter = animatableAdapter;

        recyclerView.stopScroll();
        recyclerView.setVerticalScrollBarEnabled(false);
        if (animationCallback != null) animationCallback.onStartAnimation();

        recyclerView.fastScrollAnimationRunning = true;
        if (finalAnimatableAdapter != null) finalAnimatableAdapter.onAnimationStart();

        recyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int l, int t, int r, int b, int ol, int ot, int or, int ob) {
                final ArrayList<View> incomingViews = new ArrayList<>();

                recyclerView.stopScroll();
                int n = recyclerView.getChildCount();
                int top = 0;
                int bottom = 0;
                int scrollDiff = 0;
                boolean hasSameViews = false;
                for (int i = 0; i < n; i++) {
                    View child = recyclerView.getChildAt(i);
                    incomingViews.add(child);
                    if (child.getTop() < top)
                        top = child.getTop();
                    if (child.getBottom() > bottom)
                        bottom = child.getBottom();

                    if (adapter != null && adapter.hasStableIds()) {
                        long stableId = adapter.getItemId(recyclerView.getChildAdapterPosition(child));
                        if (oldStableIds.containsKey(stableId)) {
                            View view = oldStableIds.get(stableId);
                            if (view != null) {
                                hasSameViews = true;
                                oldViews.remove(view);
                                if (animationCallback != null) {
                                    animationCallback.recycleView(view);
                                }
                                int dif = child.getTop() - view.getTop();
                                if (dif != 0) {
                                    scrollDiff = dif;
                                }
                            }
                        }
                    }
                }

                oldStableIds.clear();

                int oldH = 0;
                int oldT = Integer.MAX_VALUE;

                for (View view : oldViews) {
                    int bot = view.getBottom();
                    int topl = view.getTop();
                    if (bot > oldH) oldH = bot;
                    if (topl < oldT) oldT = topl;

                    if (view.getParent() == null) {
                        recyclerView.addView(view);
                        layoutManager.ignoreView(view);
                    }
                }

                if (oldT == Integer.MAX_VALUE) {
                    oldT = 0;
                }

                final int scrollLength;
                if (oldViews.isEmpty()) {
                    scrollLength = Math.abs(scrollDiff);
                } else {
                    int finalHeight = scrollDown ? oldH : recyclerView.getHeight() - oldT;
                    scrollLength = finalHeight + (scrollDown ? -top : bottom - recyclerView.getHeight());
                }

                if (animator != null) {
                    animator.removeAllListeners();
                    animator.cancel();
                }
                animator = ValueAnimator.ofFloat(0, 1f);
                animator.addUpdateListener(animation -> {
                    float value = ((float) animation.getAnimatedValue());
                    int size = oldViews.size();
                    for (int i = 0; i < size; i++) {
                        View view = oldViews.get(i);
                        float viewTop = view.getY();
                        float viewBottom = view.getY() + view.getMeasuredHeight();
                        if (viewBottom < 0 || viewTop > recyclerView.getMeasuredHeight()) {
                            continue;
                        }
                        if (scrollDown) {
                            view.setTranslationY(-scrollLength * value);
                        } else {
                            view.setTranslationY(scrollLength * value);
                        }
                    }

                    size = incomingViews.size();
                    for (int i = 0; i < size; i++) {
                        View view = incomingViews.get(i);
                        if (scrollDown) {
                            view.setTranslationY((scrollLength) * (1f - value));
                        } else {
                            view.setTranslationY(-(scrollLength) * (1f - value));
                        }
                    }
                    recyclerView.invalidate();
                    if (scrollListener != null) scrollListener.onScroll();
                });

                animator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if (animator == null) {
                            return;
                        }
                        recyclerView.fastScrollAnimationRunning = false;

                        for (View view : oldViews) {
                            view.setTranslationY(0);
                            layoutManager.stopIgnoringView(view);
                            recyclerView.removeView(view);
                            if (animationCallback != null) {
                                animationCallback.recycleView(view);
                            }
                        }

                        recyclerView.setVerticalScrollBarEnabled(true);

                        int n = recyclerView.getChildCount();
                        for (int i = 0; i < n; i++) {
                            View child = recyclerView.getChildAt(i);
                            child.setTranslationY(0);
                        }

                        for (View v : incomingViews) {
                            v.setTranslationY(0);
                        }

                        if (finalAnimatableAdapter != null) {
                            finalAnimatableAdapter.onAnimationEnd();
                        }

                        if (animationCallback != null) {
                            animationCallback.onEndAnimation();
                        }

                        positionToOldView.clear();

                        animator = null;
                    }
                });

                recyclerView.removeOnLayoutChangeListener(this);

                long duration;
                if (hasSameViews) {
                    duration = 600;
                } else {
                    duration = (long) (((scrollLength / (float) recyclerView.getMeasuredHeight()) + 1f) * 200L);
                    if (duration < 300) {
                        duration = 300;
                    }
                    duration = Math.min(duration, 1300);
                }

                animator.setDuration(duration);
                animator.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
                animator.start();
            }
        });
    }

    public void cancel() {
        if (animator != null) animator.cancel();
        clear();
    }

    private void clear() {
        recyclerView.setVerticalScrollBarEnabled(true);
        recyclerView.fastScrollAnimationRunning = false;
        RecyclerView.Adapter adapter = recyclerView.getAdapter();
        if (adapter instanceof AnimatableAdapter)
            ((AnimatableAdapter) adapter).onAnimationEnd();
        animator = null;

        int n = recyclerView.getChildCount();
        for (int i = 0; i < n; i++) {
            View child = recyclerView.getChildAt(i);
            child.setTranslationY(0f);
        }
    }

    public void setScrollListener(ScrollListener listener) {
        scrollListener = listener;
    }

    public void setAnimationCallback(AnimationCallback animationCallback) {
        this.animationCallback = animationCallback;
    }

    public int getScrollDirection() {
        return scrollDirection;
    }

    public void setScrollDirection(int scrollDirection) {
        this.scrollDirection = scrollDirection;
    }

    public interface ScrollListener {
        void onScroll();
    }

    public static class AnimationCallback {
        public void onStartAnimation() {
        }

        public void onEndAnimation() {
        }

        public void recycleView(View view) {

        }
    }

    public static abstract class AnimatableAdapter extends RecyclerListView.SelectionAdapter {

        private final ArrayList<Integer> rangeInserted = new ArrayList<>();
        private final ArrayList<Integer> rangeRemoved = new ArrayList<>();
        public boolean animationRunning;
        private boolean shouldNotifyDataSetChanged;

        public void onAnimationStart() {
            animationRunning = true;
            shouldNotifyDataSetChanged = false;
            rangeInserted.clear();
            rangeRemoved.clear();
        }

        public void onAnimationEnd() {
            animationRunning = false;
            if (shouldNotifyDataSetChanged || !rangeInserted.isEmpty() || !rangeRemoved.isEmpty()) {
                notifyDataSetChanged();
            }
        }
    }
}
