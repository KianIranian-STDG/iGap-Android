package net.iGap.module.customView;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.SparseIntArray;
import android.view.GestureDetector;
import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewPropertyAnimator;
import android.view.accessibility.AccessibilityEvent;
import android.widget.FrameLayout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import net.iGap.G;
import net.iGap.helper.FileLog;
import net.iGap.helper.LayoutCreator;
import net.iGap.messenger.theme.Theme;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class RecyclerListView extends RecyclerView {

    private static int[] attributes;
    private static boolean gotAttributes;
    public boolean fastScrollAnimationRunning;
    protected int selectorPosition;
    protected android.graphics.Rect selectorRect = new android.graphics.Rect();
    int emptyViewAnimateToVisibility;
    private OnItemClickListener onItemClickListener;
    private OnItemClickListenerExtended onItemClickListenerExtended;
    private OnItemLongClickListener onItemLongClickListener;
    private OnItemLongClickListenerExtended onItemLongClickListenerExtended;
    private boolean longPressCalled;
    private OnScrollListener onScrollListener;
    private OnInterceptTouchListener onInterceptTouchListener;
    private View emptyView;
    private FrameLayout overlayContainer;
    private Runnable selectChildRunnable;
    private FastScroll fastScroll;
    private SectionsAdapter sectionsAdapter;
    private boolean isHidden;
    private boolean disableHighlightState;
    private boolean allowItemsInteractionDuringAnimation = true;
    private Drawable pinnedHeaderShadowDrawable;
    private float pinnedHeaderShadowAlpha;
    private float pinnedHeaderShadowTargetAlpha;
    private long lastAlphaAnimationTime;
    private ArrayList<View> headers;
    private ArrayList<View> headersCache;
    private View pinnedHeader;
    private int currentFirst = -1;
    private int currentVisible = -1;
    private int startSection;
    private int sectionsCount;
    private int sectionsType;
    private int sectionOffset;
    private boolean hideIfEmpty = true;
    private boolean drawSelectorBehind;
    private int selectorType = 2;
    private boolean isChildViewEnabled;
    private boolean selfOnLayout;
    private boolean scrollingByUser;
    private GestureDetector gestureDetector;
    private View currentChildView;
    private int currentChildPosition;
    private boolean interceptedByChild;
    private boolean disallowInterceptTouchEvents;
    private boolean instantClick;
    private Runnable clickRunnable;
    private boolean scrollEnabled = true;
    private IntReturnCallback pendingHighlightPosition;
    private Runnable removeHighlighSelectionRunnable;
    private boolean hiddenByEmptyView;
    private boolean animateEmptyView;
    private int emptyViewAnimationType;
    private final AdapterDataObserver observer = new AdapterDataObserver() {
        @Override
        public void onChanged() {
            checkIfEmpty(true);
            currentFirst = -1;
            if (removeHighlighSelectionRunnable == null) {
                selectorRect.setEmpty();
            }
            invalidate();
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            checkIfEmpty(true);
            if (pinnedHeader != null && pinnedHeader.getAlpha() == 0) {
                currentFirst = -1;
                invalidateViews();
            }
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            checkIfEmpty(true);
        }
    };
    private int selectorRadius;
    private int topBottomSelectorRadius;

    @SuppressLint("PrivateApi")
    public RecyclerListView(Context context) {
        super(context);

        try {
            if (!gotAttributes) {
                attributes = getResourceDeclareStyleableIntArray("com.android.internal", "View");
                gotAttributes = true;
            }
            TypedArray a = context.getTheme().obtainStyledAttributes(attributes);
            @SuppressLint("DiscouragedPrivateApi")
            Method initializeScrollbars = android.view.View.class.getDeclaredMethod("initializeScrollbars", TypedArray.class);
            initializeScrollbars.invoke(this, a);
            a.recycle();
        } catch (Throwable e) {
            FileLog.e(e);
        }
        super.setOnScrollListener(new OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState != SCROLL_STATE_IDLE && currentChildView != null) {
                    if (selectChildRunnable != null) {
                        G.cancelRunOnUiThread(selectChildRunnable);
                        selectChildRunnable = null;
                    }
                    MotionEvent event = MotionEvent.obtain(0, 0, MotionEvent.ACTION_CANCEL, 0, 0, 0);
                    try {
                        gestureDetector.onTouchEvent(event);
                    } catch (Exception e) {
                        FileLog.e(e);
                    }
                    currentChildView.onTouchEvent(event);
                    event.recycle();
                    View child = currentChildView;
                    onChildPressed(currentChildView, 0, 0, false);
                    currentChildView = null;
                    removeSelection(child, null);
                    interceptedByChild = false;
                }
                if (onScrollListener != null) {
                    onScrollListener.onScrollStateChanged(recyclerView, newState);
                }
                scrollingByUser = newState == SCROLL_STATE_DRAGGING || newState == SCROLL_STATE_SETTLING;
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (onScrollListener != null) {
                    onScrollListener.onScrolled(recyclerView, dx, dy);
                }
                if (selectorPosition != NO_POSITION) {
                    selectorRect.offset(-dx, -dy);
                    invalidate();
                } else {
                    selectorRect.setEmpty();
                }
                checkSection();
            }
        });
        addOnItemTouchListener(new RecyclerListViewItemClickListener(context));
    }

    @Override
    public View findChildViewUnder(float x, float y) {
        final int count = getChildCount();
        for (int a = 0; a < 2; a++) {
            for (int i = count - 1; i >= 0; i--) {
                final View child = getChildAt(i);
                final float translationX = a == 0 ? child.getTranslationX() : 0;
                final float translationY = a == 0 ? child.getTranslationY() : 0;
                if (x >= child.getLeft() + translationX
                        && x <= child.getRight() + translationX
                        && y >= child.getTop() + translationY
                        && y <= child.getBottom() + translationY) {
                    return child;
                }
            }
        }
        return null;
    }

    protected boolean canHighlightChildAt(View child, float x, float y) {
        return true;
    }

    public void setDisableHighlightState(boolean value) {
        disableHighlightState = value;
    }

    protected View getPressedChildView() {
        return currentChildView;
    }

    protected void onChildPressed(View child, float x, float y, boolean pressed) {
        if (disableHighlightState) {
            return;
        }
        child.setPressed(pressed);
    }

    protected boolean allowSelectChildAtPosition(float x, float y) {
        return true;
    }

    protected boolean allowSelectChildAtPosition(View child) {
        return true;
    }

    private void removeSelection(View pressedChild, MotionEvent event) {
        if (pressedChild == null || selectorRect.isEmpty()) {
            return;
        }
        if (pressedChild.isEnabled()) {
            positionSelector(currentChildPosition, pressedChild);
        } else {
            selectorRect.setEmpty();
        }
    }

    public void cancelClickRunnables(boolean uncheck) {
        if (selectChildRunnable != null) {
            G.cancelRunOnUiThread(selectChildRunnable);
            selectChildRunnable = null;
        }
        if (currentChildView != null) {
            View child = currentChildView;
            if (uncheck) {
                onChildPressed(currentChildView, 0, 0, false);
            }
            currentChildView = null;
            removeSelection(child, null);
        }
        selectorRect.setEmpty();
        if (clickRunnable != null) {
            G.cancelRunOnUiThread(clickRunnable);
            clickRunnable = null;
        }
        interceptedByChild = false;
    }

    public int[] getResourceDeclareStyleableIntArray(String packageName, String name) {
        try {
            Field f = Class.forName(packageName + ".R$styleable").getField(name);
            if (f != null) {
                return (int[]) f.get(null);
            }
        } catch (Throwable t) {
            //ignore
        }
        return null;
    }

    @Override
    public void setVerticalScrollBarEnabled(boolean verticalScrollBarEnabled) {
        if (attributes != null) {
            super.setVerticalScrollBarEnabled(verticalScrollBarEnabled);
        }
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        super.onMeasure(widthSpec, heightSpec);
        if (fastScroll != null) {
            int height = getMeasuredHeight() - getPaddingTop() - getPaddingBottom();
            fastScroll.getLayoutParams().height = height;
            fastScroll.measure(MeasureSpec.makeMeasureSpec(LayoutCreator.dp(132), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (fastScroll != null) {
            selfOnLayout = true;
            t += getPaddingTop();
            if (G.isAppRtl) {
                fastScroll.layout(0, t, fastScroll.getMeasuredWidth(), t + fastScroll.getMeasuredHeight());
            } else {
                int x = getMeasuredWidth() - fastScroll.getMeasuredWidth();
                fastScroll.layout(x, t, x + fastScroll.getMeasuredWidth(), t + fastScroll.getMeasuredHeight());
            }
            selfOnLayout = false;
        }
        checkSection();
        if (pendingHighlightPosition != null) {
            highlightRowInternal(pendingHighlightPosition, false);
        }
    }

    public void checkSection() {
        if (scrollingByUser && fastScroll != null || sectionsType != 0 && sectionsAdapter != null) {
            LayoutManager layoutManager = getLayoutManager();
            if (layoutManager instanceof LinearLayoutManager) {
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
                if (linearLayoutManager.getOrientation() == LinearLayoutManager.VERTICAL) {
                    if (sectionsAdapter != null) {
                        int paddingTop = getPaddingTop();
                        if (sectionsType == 1) {
                            int childCount = getChildCount();
                            int maxBottom = 0;
                            int minBottom = Integer.MAX_VALUE;
                            View minChild = null;

                            int minBottomSection = Integer.MAX_VALUE;
                            for (int a = 0; a < childCount; a++) {
                                View child = getChildAt(a);
                                int bottom = child.getBottom();
                                if (bottom <= sectionOffset + paddingTop) {
                                    continue;
                                }
                                if (bottom < minBottom) {
                                    minBottom = bottom;
                                    minChild = child;
                                }
                                maxBottom = Math.max(maxBottom, bottom);
                                if (bottom < sectionOffset + paddingTop + LayoutCreator.dp(32)) {
                                    continue;
                                }
                                if (bottom < minBottomSection) {
                                    minBottomSection = bottom;
                                }
                            }
                            if (minChild == null) {
                                return;
                            }
                            ViewHolder holder = getChildViewHolder(minChild);
                            if (holder == null) {
                                return;
                            }

                            int firstVisibleItem = holder.getAdapterPosition();
                            int lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                            int visibleItemCount = Math.abs(lastVisibleItem - firstVisibleItem) + 1;

                            if (scrollingByUser && fastScroll != null) {
                                Adapter adapter = getAdapter();
                                if (adapter instanceof FastScrollAdapter) {
                                    fastScroll.setProgress(Math.min(1.0f, firstVisibleItem / (float) (adapter.getItemCount() - visibleItemCount + 1)));
                                }
                            }

                            headersCache.addAll(headers);
                            headers.clear();
                            if (sectionsAdapter.getItemCount() == 0) {
                                return;
                            }
                            if (currentFirst != firstVisibleItem || currentVisible != visibleItemCount) {
                                currentFirst = firstVisibleItem;
                                currentVisible = visibleItemCount;

                                sectionsCount = 1;
                                startSection = sectionsAdapter.getSectionForPosition(firstVisibleItem);
                                int itemNum = firstVisibleItem + sectionsAdapter.getCountForSection(startSection) - sectionsAdapter.getPositionInSectionForPosition(firstVisibleItem);
                                while (itemNum < firstVisibleItem + visibleItemCount) {
                                    itemNum += sectionsAdapter.getCountForSection(startSection + sectionsCount);
                                    sectionsCount++;
                                }
                            }

                            int itemNum = firstVisibleItem;
                            for (int a = startSection; a < startSection + sectionsCount; a++) {
                                View header = null;
                                if (!headersCache.isEmpty()) {
                                    header = headersCache.get(0);
                                    headersCache.remove(0);
                                }
                                header = getSectionHeaderView(a, header);
                                headers.add(header);
                                int count = sectionsAdapter.getCountForSection(a);
                                if (a == startSection) {
                                    int pos = sectionsAdapter.getPositionInSectionForPosition(itemNum);
                                    if (pos == count - 1) {
                                        header.setTag(-header.getHeight() + paddingTop);
                                    } else if (pos == count - 2) {
                                        View child = getChildAt(itemNum - firstVisibleItem);
                                        int headerTop;
                                        if (child != null) {
                                            headerTop = child.getTop() + paddingTop;
                                        } else {
                                            headerTop = -LayoutCreator.dp(100);
                                        }
                                        header.setTag(Math.min(headerTop, 0));
                                    } else {
                                        header.setTag(0);
                                    }
                                    itemNum += count - sectionsAdapter.getPositionInSectionForPosition(firstVisibleItem);
                                } else {
                                    View child = getChildAt(itemNum - firstVisibleItem);
                                    if (child != null) {
                                        header.setTag(child.getTop() + paddingTop);
                                    } else {
                                        header.setTag(-LayoutCreator.dp(100));
                                    }
                                    itemNum += count;
                                }
                            }
                        } else if (sectionsType == 2) {
                            pinnedHeaderShadowTargetAlpha = 0.0f;
                            if (sectionsAdapter.getItemCount() == 0) {
                                return;
                            }
                            int childCount = getChildCount();
                            int maxBottom = 0;
                            int minBottom = Integer.MAX_VALUE;
                            View minChild = null;

                            int minBottomSection = Integer.MAX_VALUE;
                            View minChildSection = null;
                            for (int a = 0; a < childCount; a++) {
                                View child = getChildAt(a);
                                int bottom = child.getBottom();
                                if (bottom <= sectionOffset + paddingTop) {
                                    continue;
                                }
                                if (bottom < minBottom) {
                                    minBottom = bottom;
                                    minChild = child;
                                }
                                maxBottom = Math.max(maxBottom, bottom);
                                if (bottom < sectionOffset + paddingTop + LayoutCreator.dp(32)) {
                                    continue;
                                }
                                if (bottom < minBottomSection) {
                                    minBottomSection = bottom;
                                    minChildSection = child;
                                }
                            }
                            if (minChild == null) {
                                return;
                            }
                            ViewHolder holder = getChildViewHolder(minChild);
                            if (holder == null) {
                                return;
                            }
                            int firstVisibleItem = holder.getAdapterPosition();
                            int startSection = sectionsAdapter.getSectionForPosition(firstVisibleItem);
                            if (startSection < 0) {
                                return;
                            }
                            if (currentFirst != startSection || pinnedHeader == null) {
                                pinnedHeader = getSectionHeaderView(startSection, pinnedHeader);
                                pinnedHeader.measure(MeasureSpec.makeMeasureSpec(getMeasuredWidth(), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(getMeasuredHeight(), MeasureSpec.UNSPECIFIED));
                                pinnedHeader.layout(0, 0, pinnedHeader.getMeasuredWidth(), pinnedHeader.getMeasuredHeight());
                                currentFirst = startSection;
                            }
                            if (pinnedHeader != null && minChildSection != null && minChildSection.getClass() != pinnedHeader.getClass()) {
                                pinnedHeaderShadowTargetAlpha = 1.0f;
                            }
                            int count = sectionsAdapter.getCountForSection(startSection);

                            int pos = sectionsAdapter.getPositionInSectionForPosition(firstVisibleItem);
                            int sectionOffsetY = maxBottom != 0 && maxBottom < (getMeasuredHeight() - getPaddingBottom()) ? 0 : sectionOffset;

                            if (pos == count - 1) {
                                int headerHeight = pinnedHeader.getHeight();
                                int headerTop = paddingTop;
                                if (minChild != null) {
                                    int available = minChild.getTop() - paddingTop - sectionOffset + minChild.getHeight();
                                    if (available < headerHeight) {
                                        headerTop = available - headerHeight;
                                    }
                                } else {
                                    headerTop = -LayoutCreator.dp(100);
                                }
                                if (headerTop < 0) {
                                    pinnedHeader.setTag(paddingTop + sectionOffsetY + headerTop);
                                } else {
                                    pinnedHeader.setTag(paddingTop + sectionOffsetY);
                                }
                            } else {
                                pinnedHeader.setTag(paddingTop + sectionOffsetY);
                            }

                            invalidate();
                        }
                    } else {
                        int firstVisibleItem = linearLayoutManager.findFirstVisibleItemPosition();
                        int lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                        int visibleItemCount = Math.abs(lastVisibleItem - firstVisibleItem) + 1;
                        if (firstVisibleItem == NO_POSITION) {
                            return;
                        }
                        if (scrollingByUser && fastScroll != null) {
                            Adapter adapter = getAdapter();
                            if (adapter instanceof FastScrollAdapter) {
                                fastScroll.setProgress(Math.min(1.0f, firstVisibleItem / (float) (adapter.getItemCount() - visibleItemCount + 1)));
                            }
                        }
                    }
                }
            }
        }
    }

    public OnItemClickListener getOnItemClickListener() {
        return onItemClickListener;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        onItemClickListener = listener;
    }

    public void setOnItemClickListener(OnItemClickListenerExtended listener) {
        onItemClickListenerExtended = listener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        onItemLongClickListener = listener;
        gestureDetector.setIsLongpressEnabled(listener != null);
    }

    public void setOnItemLongClickListener(OnItemLongClickListenerExtended listener) {
        onItemLongClickListenerExtended = listener;
        gestureDetector.setIsLongpressEnabled(listener != null);
    }

    protected boolean updateEmptyViewAnimated() {
        return isAttachedToWindow();
    }

    public View getEmptyView() {
        return emptyView;
    }

    public void setEmptyView(View view) {
        if (emptyView == view) {
            return;
        }
        if (emptyView != null) {
            emptyView.animate().setListener(null).cancel();
        }
        emptyView = view;
        if (animateEmptyView && emptyView != null) {
            emptyView.setVisibility(View.GONE);
        }
        if (isHidden) {
            if (emptyView != null) {
                emptyViewAnimateToVisibility = GONE;
                emptyView.setVisibility(GONE);
            }
        } else {
            emptyViewAnimateToVisibility = -1;
            checkIfEmpty(updateEmptyViewAnimated());
        }
    }

    public void invalidateViews() {
        int count = getChildCount();
        for (int a = 0; a < count; a++) {
            getChildAt(a).invalidate();
        }
    }

    public void updateFastScrollColors() {
        if (fastScroll != null) {
            fastScroll.updateColors();
        }
    }

    public void setPinnedHeaderShadowDrawable(Drawable drawable) {
        pinnedHeaderShadowDrawable = drawable;
    }

    @Override
    public boolean canScrollVertically(int direction) {
        return scrollEnabled && super.canScrollVertically(direction);
    }

    public void setScrollEnabled(boolean value) {
        scrollEnabled = value;
    }

    public void highlightRow(RecyclerListView.IntReturnCallback callback) {
        highlightRowInternal(callback, true);
    }

    private void highlightRowInternal(RecyclerListView.IntReturnCallback callback, boolean canHighlightLater) {
        if (removeHighlighSelectionRunnable != null) {
            G.cancelRunOnUiThread(removeHighlighSelectionRunnable);
            removeHighlighSelectionRunnable = null;
        }
        RecyclerView.ViewHolder holder = findViewHolderForAdapterPosition(callback.run());
        if (holder != null) {
            positionSelector(holder.getLayoutPosition(), holder.itemView);
            G.runOnUiThread(removeHighlighSelectionRunnable = () -> {
                removeHighlighSelectionRunnable = null;
                pendingHighlightPosition = null;
            }, 700);
        } else if (canHighlightLater) {
            pendingHighlightPosition = callback;
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        if (!isEnabled()) {
            return false;
        }
        if (disallowInterceptTouchEvents) {
            requestDisallowInterceptTouchEvent(true);
        }
        return onInterceptTouchListener != null && onInterceptTouchListener.onInterceptTouchEvent(e) || super.onInterceptTouchEvent(e);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (sectionsAdapter != null && pinnedHeader != null && pinnedHeader.getAlpha() != 0 && pinnedHeader.dispatchTouchEvent(ev)) {
            return true;
        }
        return super.dispatchTouchEvent(ev);
    }

    private void checkIfEmpty(boolean animated) {
        if (isHidden) {
            return;
        }
        if (getAdapter() == null || emptyView == null) {
            if (hiddenByEmptyView && getVisibility() != VISIBLE) {
                setVisibility(VISIBLE);
                hiddenByEmptyView = false;
            }
            return;
        }
        boolean emptyViewVisible = emptyViewIsVisible();
        int newVisibility = emptyViewVisible ? VISIBLE : GONE;
        if (!animateEmptyView) {
            animated = false;
        }
        if (animated) {
            if (emptyViewAnimateToVisibility != newVisibility) {
                emptyViewAnimateToVisibility = newVisibility;
                if (newVisibility == VISIBLE) {
                    emptyView.animate().setListener(null).cancel();
                    if (emptyView.getVisibility() == GONE) {
                        emptyView.setVisibility(VISIBLE);
                        emptyView.setAlpha(0);
                        if (emptyViewAnimationType == 1) {
                            emptyView.setScaleX(0.7f);
                            emptyView.setScaleY(0.7f);
                        }
                    }
                    emptyView.animate().alpha(1f).scaleX(1).scaleY(1).setDuration(150).start();
                } else {
                    if (emptyView.getVisibility() != GONE) {
                        ViewPropertyAnimator animator = emptyView.animate().alpha(0);
                        if (emptyViewAnimationType == 1) {
                            animator.scaleY(0.7f).scaleX(0.7f);
                        }
                        animator.setDuration(150).setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                if (emptyView != null) {
                                    emptyView.setVisibility(GONE);
                                }
                            }
                        }).start();
                    }
                }
            }
        } else {
            emptyViewAnimateToVisibility = newVisibility;
            emptyView.setVisibility(newVisibility);
            emptyView.setAlpha(1f);
        }
        if (hideIfEmpty) {
            newVisibility = emptyViewVisible ? INVISIBLE : VISIBLE;
            if (getVisibility() != newVisibility) {
                setVisibility(newVisibility);
            }
            hiddenByEmptyView = true;
        }
    }

    protected boolean emptyViewIsVisible() {
        if (getAdapter() == null || isFastScrollAnimationRunning()) {
            return false;
        }
        return getAdapter().getItemCount() == 0;
    }

    public void hide() {
        if (isHidden) {
            return;
        }
        isHidden = true;
        if (getVisibility() != GONE) {
            setVisibility(GONE);
        }
        if (emptyView != null && emptyView.getVisibility() != GONE) {
            emptyView.setVisibility(GONE);
        }
    }

    public void show() {
        if (!isHidden) {
            return;
        }
        isHidden = false;
        checkIfEmpty(false);
    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        if (visibility != VISIBLE) {
            hiddenByEmptyView = false;
        }
    }

    public void setHideIfEmpty(boolean value) {
        hideIfEmpty = value;
    }

    public OnScrollListener getOnScrollListener() {
        return onScrollListener;
    }

    @Override
    public void setOnScrollListener(OnScrollListener listener) {
        onScrollListener = listener;
    }

    public void setOnInterceptTouchListener(OnInterceptTouchListener listener) {
        onInterceptTouchListener = listener;
    }

    public void setInstantClick(boolean value) {
        instantClick = value;
    }

    public void setDisallowInterceptTouchEvents(boolean value) {
        disallowInterceptTouchEvents = value;
    }

    public void setFastScrollEnabled() {
        fastScroll = new FastScroll(getContext());
        if (getParent() != null) {
            ((ViewGroup) getParent()).addView(fastScroll);
        }
    }

    public void setFastScrollVisible(boolean value) {
        if (fastScroll == null) {
            return;
        }
        fastScroll.setVisibility(value ? VISIBLE : GONE);
    }

    public void setSectionsType(int type) {
        sectionsType = type;
        if (sectionsType == 1) {
            headers = new ArrayList<>();
            headersCache = new ArrayList<>();
        }
    }

    public void setPinnedSectionOffsetY(int offset) {
        sectionOffset = offset;
        invalidate();
    }

    private void positionSelector(int position, View sel) {
        positionSelector(position, sel, false, -1, -1);
    }

    private void positionSelector(int position, View sel, boolean manageHotspot, float x, float y) {
        if (removeHighlighSelectionRunnable != null) {
            G.cancelRunOnUiThread(removeHighlighSelectionRunnable);
            removeHighlighSelectionRunnable = null;
            pendingHighlightPosition = null;
        }
        final boolean positionChanged = position != selectorPosition;
        int bottomPadding;
        if (getAdapter() instanceof SelectionAdapter) {
            bottomPadding = ((SelectionAdapter) getAdapter()).getSelectionBottomPadding(sel);
        } else {
            bottomPadding = 0;
        }
        if (position != NO_POSITION) {
            selectorPosition = position;
        }
        selectorRect.set(sel.getLeft(), sel.getTop(), sel.getRight(), sel.getBottom() - bottomPadding);

        final boolean enabled = sel.isEnabled();
        if (isChildViewEnabled != enabled) {
            isChildViewEnabled = enabled;
        }
    }

    public void setAllowItemsInteractionDuringAnimation(boolean value) {
        allowItemsInteractionDuringAnimation = value;
    }

    public void hideSelector(boolean animated) {
        if (currentChildView != null) {
            View child = currentChildView;
            onChildPressed(currentChildView, 0, 0, false);
            currentChildView = null;
            if (animated) {
                removeSelection(child, null);
            }
        }
        if (!animated) {
            selectorRect.setEmpty();
        }
    }

    @Override
    public void onChildAttachedToWindow(View child) {
        if (getAdapter() instanceof SelectionAdapter) {
            ViewHolder holder = findContainingViewHolder(child);
            if (holder != null) {
                child.setEnabled(((SelectionAdapter) getAdapter()).isEnabled(holder));
            }
        } else {
            child.setEnabled(false);
        }
        super.onChildAttachedToWindow(child);
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (fastScroll != null && fastScroll.getParent() != getParent()) {
            ViewGroup parent = (ViewGroup) fastScroll.getParent();
            if (parent != null) {
                parent.removeView(fastScroll);
            }
            parent = (ViewGroup) getParent();
            parent.addView(fastScroll);
        }
    }

    @Override
    public void setAdapter(Adapter adapter) {
        final Adapter oldAdapter = getAdapter();
        if (oldAdapter != null) {
            oldAdapter.unregisterAdapterDataObserver(observer);
        }
        if (headers != null) {
            headers.clear();
            headersCache.clear();
        }
        currentFirst = -1;
        selectorPosition = NO_POSITION;
        selectorRect.setEmpty();
        pinnedHeader = null;
        if (adapter instanceof SectionsAdapter) {
            sectionsAdapter = (SectionsAdapter) adapter;
        } else {
            sectionsAdapter = null;
        }
        super.setAdapter(adapter);
        if (adapter != null) {
            adapter.registerAdapterDataObserver(observer);
        }
        checkIfEmpty(false);
    }

    @Override
    public void stopScroll() {
        try {
            super.stopScroll();
        } catch (NullPointerException ignore) {

        }
    }

    @Override
    public boolean dispatchNestedPreScroll(int dx, int dy, int[] consumed, int[] offsetInWindow, int type) {
        if (longPressCalled) {
            if (onItemLongClickListenerExtended != null) {
                onItemLongClickListenerExtended.onMove(dx, dy);
            }
            consumed[0] = dx;
            consumed[1] = dy;
            return true;
        }
        return super.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow, type);
    }

    @Override
    public boolean hasOverlappingRendering() {
        return false;
    }

    private View getSectionHeaderView(int section, View oldView) {
        boolean shouldLayout = oldView == null;
        View view = sectionsAdapter.getSectionHeaderView(section, oldView);
        if (shouldLayout) {
            ensurePinnedHeaderLayout(view, false);
        }
        return view;
    }

    private void ensurePinnedHeaderLayout(View header, boolean forceLayout) {
        if (header == null) {
            return;
        }
        if (header.isLayoutRequested() || forceLayout) {
            if (sectionsType == 1) {
                ViewGroup.LayoutParams layoutParams = header.getLayoutParams();
                int heightSpec = MeasureSpec.makeMeasureSpec(layoutParams.height, MeasureSpec.EXACTLY);
                int widthSpec = MeasureSpec.makeMeasureSpec(layoutParams.width, MeasureSpec.EXACTLY);
                try {
                    header.measure(widthSpec, heightSpec);
                } catch (Exception e) {
                    FileLog.e(e);
                }
            } else if (sectionsType == 2) {
                int widthSpec = MeasureSpec.makeMeasureSpec(getMeasuredWidth(), MeasureSpec.EXACTLY);
                int heightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
                try {
                    header.measure(widthSpec, heightSpec);
                } catch (Exception e) {
                    FileLog.e(e);
                }
            }
            header.layout(0, 0, header.getMeasuredWidth(), header.getMeasuredHeight());
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (overlayContainer != null) {
            overlayContainer.requestLayout();
        }
        if (sectionsType == 1) {
            if (sectionsAdapter == null || headers.isEmpty()) {
                return;
            }
            for (int a = 0; a < headers.size(); a++) {
                View header = headers.get(a);
                ensurePinnedHeaderLayout(header, true);
            }
        } else if (sectionsType == 2) {
            if (sectionsAdapter == null || pinnedHeader == null) {
                return;
            }
            ensurePinnedHeaderLayout(pinnedHeader, true);
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (overlayContainer != null) {
            overlayContainer.draw(canvas);
        }
        if (sectionsType == 1) {
            if (sectionsAdapter != null && !headers.isEmpty()) {
                for (int a = 0; a < headers.size(); a++) {
                    View header = headers.get(a);
                    int saveCount = canvas.save();
                    int top = (Integer) header.getTag();
                    canvas.translate(G.isAppRtl ? getWidth() - header.getWidth() : 0, top);
                    canvas.clipRect(0, 0, getWidth(), header.getMeasuredHeight());
                    header.draw(canvas);
                    canvas.restoreToCount(saveCount);
                }
            }
        } else if (sectionsType == 2) {
            if (sectionsAdapter != null && pinnedHeader != null && pinnedHeader.getAlpha() != 0) {
                int saveCount = canvas.save();
                int top = (Integer) pinnedHeader.getTag();
                canvas.translate(G.isAppRtl ? getWidth() - pinnedHeader.getWidth() : 0, top);
                if (pinnedHeaderShadowDrawable != null) {
                    pinnedHeaderShadowDrawable.setBounds(0, pinnedHeader.getMeasuredHeight(), getWidth(), pinnedHeader.getMeasuredHeight() + pinnedHeaderShadowDrawable.getIntrinsicHeight());
                    pinnedHeaderShadowDrawable.setAlpha((int) (255 * pinnedHeaderShadowAlpha));
                    pinnedHeaderShadowDrawable.draw(canvas);

                    long newTime = SystemClock.elapsedRealtime();
                    long dt = Math.min(20, newTime - lastAlphaAnimationTime);
                    lastAlphaAnimationTime = newTime;
                    if (pinnedHeaderShadowAlpha < pinnedHeaderShadowTargetAlpha) {
                        pinnedHeaderShadowAlpha += dt / 180.0f;
                        if (pinnedHeaderShadowAlpha > pinnedHeaderShadowTargetAlpha) {
                            pinnedHeaderShadowAlpha = pinnedHeaderShadowTargetAlpha;
                        }
                        invalidate();
                    } else if (pinnedHeaderShadowAlpha > pinnedHeaderShadowTargetAlpha) {
                        pinnedHeaderShadowAlpha -= dt / 180.0f;
                        if (pinnedHeaderShadowAlpha < pinnedHeaderShadowTargetAlpha) {
                            pinnedHeaderShadowAlpha = pinnedHeaderShadowTargetAlpha;
                        }
                        invalidate();
                    }
                }
                canvas.clipRect(0, 0, getWidth(), pinnedHeader.getMeasuredHeight());
                pinnedHeader.draw(canvas);
                canvas.restoreToCount(saveCount);
            }
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        selectorPosition = NO_POSITION;
        selectorRect.setEmpty();
    }

    public void addOverlayView(View view, FrameLayout.LayoutParams layoutParams) {
        if (overlayContainer == null) {
            overlayContainer = new FrameLayout(getContext()) {
                @Override
                public void requestLayout() {
                    super.requestLayout();
                    try {
                        final int measuredWidth = RecyclerListView.this.getMeasuredWidth();
                        final int measuredHeight = RecyclerListView.this.getMeasuredHeight();
                        measure(MeasureSpec.makeMeasureSpec(measuredWidth, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(measuredHeight, MeasureSpec.EXACTLY));
                        layout(0, 0, overlayContainer.getMeasuredWidth(), overlayContainer.getMeasuredHeight());
                    } catch (Exception ignored) {
                    }
                }
            };
        }
        overlayContainer.addView(view, layoutParams);
    }

    public void removeOverlayView(View view) {
        if (overlayContainer != null) {
            overlayContainer.removeView(view);
        }
    }

    public ArrayList<View> getHeaders() {
        return headers;
    }

    public ArrayList<View> getHeadersCache() {
        return headersCache;
    }

    public View getPinnedHeader() {
        return pinnedHeader;
    }

    public boolean isFastScrollAnimationRunning() {
        return fastScrollAnimationRunning;
    }

    @Override
    public void requestLayout() {
        if (fastScrollAnimationRunning) {
            return;
        }
        super.requestLayout();
    }

    public void setAnimateEmptyView(boolean animate, int emptyViewAnimationType) {
        animateEmptyView = animate;
        this.emptyViewAnimationType = emptyViewAnimationType;
    }

    @Override
    public void setTranslationY(float translationY) {
        super.setTranslationY(translationY);
        if (fastScroll != null) {
            fastScroll.setTranslationY(translationY);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public interface OnItemClickListenerExtended {
        void onItemClick(View view, int position, float x, float y);
    }

    public interface OnItemLongClickListener {
        boolean onItemClick(View view, int position);
    }

    public interface OnItemLongClickListenerExtended {
        boolean onItemClick(View view, int position, float x, float y);

        void onMove(float dx, float dy);

        void onLongClickRelease();
    }

    public interface OnInterceptTouchListener {
        boolean onInterceptTouchEvent(MotionEvent event);
    }

    public interface IntReturnCallback {
        int run();
    }

    public abstract static class SelectionAdapter extends Adapter {
        public abstract boolean isEnabled(ViewHolder holder);

        public int getSelectionBottomPadding(View view) {
            return 0;
        }
    }

    public abstract static class FastScrollAdapter extends SelectionAdapter {
        public abstract String getLetter(int position);

        public abstract int getPositionForScrollProgress(float progress);
    }

    public abstract static class SectionsAdapter extends FastScrollAdapter {

        private SparseIntArray sectionPositionCache;
        private SparseIntArray sectionCache;
        private SparseIntArray sectionCountCache;
        private int sectionCount;
        private int count;

        public SectionsAdapter() {
            super();
            cleanupCache();
        }

        private void cleanupCache() {
            if (sectionCache == null) {
                sectionCache = new SparseIntArray();
                sectionPositionCache = new SparseIntArray();
                sectionCountCache = new SparseIntArray();
            } else {
                sectionCache.clear();
                sectionPositionCache.clear();
                sectionCountCache.clear();
            }
            count = -1;
            sectionCount = -1;
        }

        public void notifySectionsChanged() {
            cleanupCache();
        }

//        @Override
//        public void notifyDataSetChanged() {
//            cleanupCache();
//            super.notifyDataSetChanged();
//        }

        @Override
        public boolean isEnabled(ViewHolder holder) {
            int position = holder.getAdapterPosition();
            return isEnabled(getSectionForPosition(position), getPositionInSectionForPosition(position));
        }

        @Override
        public int getItemCount() {
            if (count >= 0) {
                return count;
            }
            count = 0;
            for (int i = 0, N = internalGetSectionCount(); i < N; i++) {
                count += internalGetCountForSection(i);
            }
            return count;
        }

        public final Object getItem(int position) {
            return getItem(getSectionForPosition(position), getPositionInSectionForPosition(position));
        }

        public final int getItemViewType(int position) {
            return getItemViewType(getSectionForPosition(position), getPositionInSectionForPosition(position));
        }

        @Override
        public final void onBindViewHolder(ViewHolder holder, int position) {
            onBindViewHolder(getSectionForPosition(position), getPositionInSectionForPosition(position), holder);
        }

        private int internalGetCountForSection(int section) {
            int cachedSectionCount = sectionCountCache.get(section, Integer.MAX_VALUE);
            if (cachedSectionCount != Integer.MAX_VALUE) {
                return cachedSectionCount;
            }
            int sectionCount = getCountForSection(section);
            sectionCountCache.put(section, sectionCount);
            return sectionCount;
        }

        private int internalGetSectionCount() {
            if (sectionCount >= 0) {
                return sectionCount;
            }
            sectionCount = getSectionCount();
            return sectionCount;
        }

        public final int getSectionForPosition(int position) {
            int cachedSection = sectionCache.get(position, Integer.MAX_VALUE);
            if (cachedSection != Integer.MAX_VALUE) {
                return cachedSection;
            }
            int sectionStart = 0;
            for (int i = 0, N = internalGetSectionCount(); i < N; i++) {
                int sectionCount = internalGetCountForSection(i);
                int sectionEnd = sectionStart + sectionCount;
                if (position >= sectionStart && position < sectionEnd) {
                    sectionCache.put(position, i);
                    return i;
                }
                sectionStart = sectionEnd;
            }
            return -1;
        }

        public int getPositionInSectionForPosition(int position) {
            int cachedPosition = sectionPositionCache.get(position, Integer.MAX_VALUE);
            if (cachedPosition != Integer.MAX_VALUE) {
                return cachedPosition;
            }
            int sectionStart = 0;
            for (int i = 0, N = internalGetSectionCount(); i < N; i++) {
                int sectionCount = internalGetCountForSection(i);
                int sectionEnd = sectionStart + sectionCount;
                if (position >= sectionStart && position < sectionEnd) {
                    int positionInSection = position - sectionStart;
                    sectionPositionCache.put(position, positionInSection);
                    return positionInSection;
                }
                sectionStart = sectionEnd;
            }
            return -1;
        }

        public abstract int getSectionCount();

        public abstract int getCountForSection(int section);

        public abstract boolean isEnabled(int section, int row);

        public abstract int getItemViewType(int section, int position);

        public abstract Object getItem(int section, int position);

        public abstract void onBindViewHolder(int section, int position, ViewHolder holder);

        public abstract View getSectionHeaderView(int section, View view);
    }

    public static class Holder extends ViewHolder {

        public Holder(View itemView) {
            super(itemView);
        }
    }

    public static class FoucsableOnTouchListener implements OnTouchListener {
        private float x;
        private float y;
        private boolean onFocus;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            ViewParent parent = v.getParent();
            if (parent == null) {
                return false;
            }
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                x = event.getX();
                y = event.getY();
                onFocus = true;
                parent.requestDisallowInterceptTouchEvent(true);
            }
            if (event.getAction() == MotionEvent.ACTION_MOVE) {
                float dx = (x - event.getX());
                float dy = (y - event.getY());
                float touchSlop = ViewConfiguration.get(v.getContext()).getScaledTouchSlop();
                if (onFocus && Math.sqrt(dx * dx + dy * dy) > touchSlop) {
                    onFocus = false;
                    parent.requestDisallowInterceptTouchEvent(false);
                }
            } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                onFocus = false;
                parent.requestDisallowInterceptTouchEvent(false);
            }
            return false;
        }
    }

    private class FastScroll extends View {

        private final RectF rect = new RectF();
        private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final TextPaint letterPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        private final Path path = new Path();
        private final float[] radii = new float[8];
        private final int[] colors = new int[6];
        private final int scrollX;
        private float progress;
        private float lastY;
        private float startDy;
        private boolean pressed;
        private StaticLayout letterLayout;
        private StaticLayout oldLetterLayout;
        private String currentLetter;
        private float textX;
        private float textY;
        private float bubbleProgress;
        private long lastUpdateTime;

        public FastScroll(Context context) {
            super(context);

            letterPaint.setTextSize(LayoutCreator.dp(45));
            for (int a = 0; a < 8; a++) {
                radii[a] = LayoutCreator.dp(44);
            }

            scrollX = G.isAppRtl ? LayoutCreator.dp(10) : LayoutCreator.dp(117);
            updateColors();
        }

        private void updateColors() {
            int inactive = Theme.getColor(Theme.key_light_gray);
            int active = Theme.getColor(Theme.key_title_text);
            paint.setColor(inactive);
            letterPaint.setColor(Theme.getColor(Theme.key_white));
            colors[0] = Color.red(inactive);
            colors[1] = Color.red(active);
            colors[2] = Color.green(inactive);
            colors[3] = Color.green(active);
            colors[4] = Color.blue(inactive);
            colors[5] = Color.blue(active);
            invalidate();
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    float x = event.getX();
                    lastY = event.getY();
                    float currentY = (float) Math.ceil((getMeasuredHeight() - LayoutCreator.dp(24 + 30)) * progress) + LayoutCreator.dp(12);
                    if (G.isAppRtl && x > LayoutCreator.dp(25) || !G.isAppRtl && x < LayoutCreator.dp(107) || lastY < currentY || lastY > currentY + LayoutCreator.dp(30)) {
                        return false;
                    }
                    startDy = lastY - currentY;
                    pressed = true;
                    lastUpdateTime = System.currentTimeMillis();
                    getCurrentLetter();
                    invalidate();
                    return true;
                case MotionEvent.ACTION_MOVE:
                    if (!pressed) {
                        return true;
                    }
                    float newY = event.getY();
                    float minY = LayoutCreator.dp(12) + startDy;
                    float maxY = getMeasuredHeight() - LayoutCreator.dp(12 + 30) + startDy;
                    if (newY < minY) {
                        newY = minY;
                    } else if (newY > maxY) {
                        newY = maxY;
                    }
                    float dy = newY - lastY;
                    lastY = newY;
                    progress += dy / (getMeasuredHeight() - LayoutCreator.dp(24 + 30));
                    if (progress < 0) {
                        progress = 0;
                    } else if (progress > 1) {
                        progress = 1;
                    }
                    getCurrentLetter();
                    invalidate();
                    return true;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    pressed = false;
                    lastUpdateTime = System.currentTimeMillis();
                    invalidate();
                    return true;
            }
            return super.onTouchEvent(event);
        }

        private void getCurrentLetter() {
            LayoutManager layoutManager = getLayoutManager();
            if (layoutManager instanceof LinearLayoutManager) {
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
                if (linearLayoutManager.getOrientation() == LinearLayoutManager.VERTICAL) {
                    Adapter adapter = getAdapter();
                    if (adapter instanceof FastScrollAdapter) {
                        FastScrollAdapter fastScrollAdapter = (FastScrollAdapter) adapter;
                        int position = fastScrollAdapter.getPositionForScrollProgress(progress);
                        linearLayoutManager.scrollToPositionWithOffset(position, sectionOffset);
                        String newLetter = fastScrollAdapter.getLetter(position);
                        if (newLetter == null) {
                            if (letterLayout != null) {
                                oldLetterLayout = letterLayout;
                            }
                            letterLayout = null;
                        } else if (!newLetter.equals(currentLetter)) {
                            letterLayout = new StaticLayout(newLetter, letterPaint, 1000, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                            oldLetterLayout = null;
                            if (letterLayout.getLineCount() > 0) {
                                float lWidth = letterLayout.getLineWidth(0);
                                float lleft = letterLayout.getLineLeft(0);
                                if (G.isAppRtl) {
                                    textX = LayoutCreator.dp(10) + (LayoutCreator.dp(88) - letterLayout.getLineWidth(0)) / 2 - letterLayout.getLineLeft(0);
                                } else {
                                    textX = (LayoutCreator.dp(88) - letterLayout.getLineWidth(0)) / 2 - letterLayout.getLineLeft(0);
                                }
                                textY = (LayoutCreator.dp(88) - letterLayout.getHeight()) / 2;
                            }
                        }
                    }
                }
            }
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            setMeasuredDimension(LayoutCreator.dp(132), MeasureSpec.getSize(heightMeasureSpec));
        }

        @Override
        protected void onDraw(Canvas canvas) {
            paint.setColor(Color.argb(255, colors[0] + (int) ((colors[1] - colors[0]) * bubbleProgress), colors[2] + (int) ((colors[3] - colors[2]) * bubbleProgress), colors[4] + (int) ((colors[5] - colors[4]) * bubbleProgress)));
            int y = (int) Math.ceil((getMeasuredHeight() - LayoutCreator.dp(24 + 30)) * progress);
            rect.set(scrollX, LayoutCreator.dp(12) + y, scrollX + LayoutCreator.dp(5), LayoutCreator.dp(12 + 30) + y);
            canvas.drawRoundRect(rect, LayoutCreator.dp(2), LayoutCreator.dp(2), paint);
            if ((pressed || bubbleProgress != 0)) {
                paint.setAlpha((int) (255 * bubbleProgress));
                int progressY = y + LayoutCreator.dp(30);
                y -= LayoutCreator.dp(46);
                float diff = 0;
                if (y <= LayoutCreator.dp(12)) {
                    diff = LayoutCreator.dp(12) - y;
                    y = LayoutCreator.dp(12);
                }
                float raduisTop;
                float raduisBottom;
                canvas.translate(LayoutCreator.dp(10), y);
                if (diff <= LayoutCreator.dp(29)) {
                    raduisTop = LayoutCreator.dp(44);
                    raduisBottom = LayoutCreator.dp(4) + (diff / LayoutCreator.dp(29)) * LayoutCreator.dp(40);
                } else {
                    diff -= LayoutCreator.dp(29);
                    raduisBottom = LayoutCreator.dp(44);
                    raduisTop = LayoutCreator.dp(4) + (1.0f - diff / LayoutCreator.dp(29)) * LayoutCreator.dp(40);
                }
                if (G.isAppRtl && (radii[0] != raduisTop || radii[6] != raduisBottom) || !G.isAppRtl && (radii[2] != raduisTop || radii[4] != raduisBottom)) {
                    if (G.isAppRtl) {
                        radii[0] = radii[1] = raduisTop;
                        radii[6] = radii[7] = raduisBottom;
                    } else {
                        radii[2] = radii[3] = raduisTop;
                        radii[4] = radii[5] = raduisBottom;
                    }
                    path.reset();
                    rect.set(G.isAppRtl ? LayoutCreator.dp(10) : 0, 0, LayoutCreator.dp(G.isAppRtl ? 98 : 88), LayoutCreator.dp(88));
                    path.addRoundRect(rect, radii, Path.Direction.CW);
                    path.close();
                }
                StaticLayout layoutToDraw = letterLayout != null ? letterLayout : oldLetterLayout;
                if (layoutToDraw != null) {
                    canvas.save();
                    canvas.scale(bubbleProgress, bubbleProgress, scrollX, progressY - y);
                    canvas.drawPath(path, paint);
                    canvas.translate(textX, textY);
                    layoutToDraw.draw(canvas);
                    canvas.restore();
                }
            }
            if ((pressed && letterLayout != null && bubbleProgress < 1.0f) || (!pressed || letterLayout == null) && bubbleProgress > 0.0f) {
                long newTime = System.currentTimeMillis();
                long dt = (newTime - lastUpdateTime);
                if (dt < 0 || dt > 17) {
                    dt = 17;
                }
                lastUpdateTime = newTime;
                invalidate();
                if (pressed && letterLayout != null) {
                    bubbleProgress += dt / 120.0f;
                    if (bubbleProgress > 1.0f) {
                        bubbleProgress = 1.0f;
                    }
                } else {
                    bubbleProgress -= dt / 120.0f;
                    if (bubbleProgress < 0.0f) {
                        bubbleProgress = 0.0f;
                    }
                }
            }
        }

        @Override
        public void layout(int l, int t, int r, int b) {
            if (!selfOnLayout) {
                return;
            }
            super.layout(l, t, r, b);
        }

        private void setProgress(float value) {
            progress = value;
            invalidate();
        }
    }

    private class RecyclerListViewItemClickListener implements OnItemTouchListener {

        public RecyclerListViewItemClickListener(Context context) {
            gestureDetector = new GestureDetector(context, new GestureDetector.OnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    if (currentChildView != null && (onItemClickListener != null || onItemClickListenerExtended != null)) {
                        final float x = e.getX();
                        final float y = e.getY();
                        onChildPressed(currentChildView, x, y, true);
                        final View view = currentChildView;
                        final int position = currentChildPosition;
                        if (instantClick && position != -1) {
                            view.playSoundEffect(SoundEffectConstants.CLICK);
                            view.sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_CLICKED);
                            if (onItemClickListener != null) {
                                onItemClickListener.onItemClick(view, position);
                            } else if (onItemClickListenerExtended != null) {
                                onItemClickListenerExtended.onItemClick(view, position, x - view.getX(), y - view.getY());
                            }
                        }
                        G.runOnUiThread(clickRunnable = new Runnable() {
                            @Override
                            public void run() {
                                if (this == clickRunnable) {
                                    clickRunnable = null;
                                }
                                if (view != null) {
                                    onChildPressed(view, 0, 0, false);
                                    if (!instantClick) {
                                        view.playSoundEffect(SoundEffectConstants.CLICK);
                                        view.sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_CLICKED);
                                        if (position != -1) {
                                            if (onItemClickListener != null) {
                                                onItemClickListener.onItemClick(view, position);
                                            } else if (onItemClickListenerExtended != null) {
                                                onItemClickListenerExtended.onItemClick(view, position, x - view.getX(), y - view.getY());
                                            }
                                        }
                                    }
                                }
                            }
                        }, ViewConfiguration.getPressedStateDuration());

                        if (selectChildRunnable != null) {
                            View pressedChild = currentChildView;
                            G.cancelRunOnUiThread(selectChildRunnable);
                            selectChildRunnable = null;
                            currentChildView = null;
                            interceptedByChild = false;
                            removeSelection(pressedChild, e);
                        }
                    }
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent event) {
                    if (currentChildView == null || currentChildPosition == -1 || onItemLongClickListener == null && onItemLongClickListenerExtended == null) {
                        return;
                    }
                    View child = currentChildView;
                    if (onItemLongClickListener != null) {
                        if (onItemLongClickListener.onItemClick(currentChildView, currentChildPosition)) {
                            child.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                            child.sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_LONG_CLICKED);
                        }
                    } else if (onItemLongClickListenerExtended != null) {
                        if (onItemLongClickListenerExtended.onItemClick(currentChildView, currentChildPosition, event.getX() - currentChildView.getX(), event.getY() - currentChildView.getY())) {
                            child.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                            child.sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_LONG_CLICKED);
                            longPressCalled = true;
                        }
                    }
                }

                @Override
                public boolean onDown(MotionEvent e) {
                    return false;
                }

                @Override
                public void onShowPress(MotionEvent e) {

                }

                @Override
                public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                    return false;
                }

                @Override
                public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                    return false;
                }
            });
            gestureDetector.setIsLongpressEnabled(false);
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView view, MotionEvent event) {
            int action = event.getActionMasked();
            boolean isScrollIdle = RecyclerListView.this.getScrollState() == RecyclerListView.SCROLL_STATE_IDLE;

            if ((action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_POINTER_DOWN) && currentChildView == null && isScrollIdle) {
                float ex = event.getX();
                float ey = event.getY();
                longPressCalled = false;
                ItemAnimator animator = getItemAnimator();
                if ((allowItemsInteractionDuringAnimation || animator == null || !animator.isRunning()) && allowSelectChildAtPosition(ex, ey)) {
                    View v = findChildViewUnder(ex, ey);
                    if (v != null && allowSelectChildAtPosition(v)) {
                        currentChildView = v;
                    }
                }
                if (currentChildView instanceof ViewGroup) {
                    float x = event.getX() - currentChildView.getLeft();
                    float y = event.getY() - currentChildView.getTop();
                    ViewGroup viewGroup = (ViewGroup) currentChildView;
                    final int count = viewGroup.getChildCount();
                    for (int i = count - 1; i >= 0; i--) {
                        final View child = viewGroup.getChildAt(i);
                        if (x >= child.getLeft() && x <= child.getRight() && y >= child.getTop() && y <= child.getBottom()) {
                            if (child.isClickable()) {
                                currentChildView = null;
                                break;
                            }
                        }
                    }
                }
                currentChildPosition = -1;
                if (currentChildView != null) {
                    currentChildPosition = view.getChildPosition(currentChildView);
                    MotionEvent childEvent = MotionEvent.obtain(0, 0, event.getActionMasked(), event.getX() - currentChildView.getLeft(), event.getY() - currentChildView.getTop(), 0);
                    if (currentChildView.onTouchEvent(childEvent)) {
                        interceptedByChild = true;
                    }
                    childEvent.recycle();
                }
            }

            if (currentChildView != null && !interceptedByChild) {
                try {
                    if (event != null) {
                        gestureDetector.onTouchEvent(event);
                    }
                } catch (Exception e) {
                    FileLog.e(e);
                }
            }

            if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_POINTER_DOWN) {
                if (!interceptedByChild && currentChildView != null) {
                    float x = event.getX();
                    float y = event.getY();
                    selectChildRunnable = () -> {
                        if (selectChildRunnable != null && currentChildView != null) {
                            onChildPressed(currentChildView, x, y, true);
                            selectChildRunnable = null;
                        }
                    };
                    G.runOnUiThread(selectChildRunnable, ViewConfiguration.getTapTimeout());
                    if (currentChildView.isEnabled() && canHighlightChildAt(currentChildView, x - currentChildView.getX(), y - currentChildView.getY())) {
                        positionSelector(currentChildPosition, currentChildView);
                    } else {
                        selectorRect.setEmpty();
                    }
                } else {
                    selectorRect.setEmpty();
                }
            } else if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_POINTER_UP || action == MotionEvent.ACTION_CANCEL || !isScrollIdle) {
                if (currentChildView != null) {
                    if (selectChildRunnable != null) {
                        G.cancelRunOnUiThread(selectChildRunnable);
                        selectChildRunnable = null;
                    }
                    View pressedChild = currentChildView;
                    onChildPressed(currentChildView, 0, 0, false);
                    currentChildView = null;
                    interceptedByChild = false;
                    removeSelection(pressedChild, event);

                    if ((action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_POINTER_UP || action == MotionEvent.ACTION_CANCEL) && onItemLongClickListenerExtended != null && longPressCalled) {
                        onItemLongClickListenerExtended.onLongClickRelease();
                        longPressCalled = false;
                    }
                }
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView view, MotionEvent event) {

        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
            cancelClickRunnables(true);
        }
    }
}
