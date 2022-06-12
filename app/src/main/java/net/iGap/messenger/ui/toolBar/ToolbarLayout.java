package net.iGap.messenger.ui.toolBar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.Gravity;
import android.view.HapticFeedbackConstants;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import com.google.android.exoplayer2.util.Log;

import net.iGap.G;
import net.iGap.R;
import net.iGap.fragments.BaseFragment;
import net.iGap.helper.FileLog;
import net.iGap.helper.LayoutCreator;
import net.iGap.libs.emojiKeyboard.View.CubicBezierInterpolator;
import net.iGap.messenger.theme.Theme;

import java.util.ArrayList;

import static net.iGap.messenger.ui.toolBar.DrawerLayoutContainer.getPixelsInCM;
import static net.iGap.module.AndroidUtils.getViewInset;
import static net.iGap.module.AndroidUtils.hideKeyboard;
import static net.iGap.module.AndroidUtils.statusBarHeight;

public class ToolbarLayout extends FrameLayout {

    public ToolbarLayout(@NonNull Context context) {
        super(context);
        parentActivity = (Activity) context;

        if (layerShadowDrawable == null) {
            layerShadowDrawable = getResources().getDrawable(R.drawable.layer_shadow);
            headerShadowDrawable = getResources().getDrawable(R.drawable.header_shadow).mutate();
            scrimPaint = new Paint();
        }
    }

    public interface ToolBarLayoutDelegate {
        boolean onPreIme();
        boolean needPresentFragment(BaseFragment fragment, boolean removeLast, boolean forceWithoutAnimation, ToolbarLayout layout);
        boolean needAddFragmentToStack(BaseFragment fragment, ToolbarLayout layout);
        boolean needCloseLastFragment(ToolbarLayout layout);
        void onRebuildAllFragments(ToolbarLayout layout, boolean last);
    }

    public class LayoutContainer extends FrameLayout {

        private Rect rect = new Rect();
        private boolean isKeyboardVisible;

        private int fragmentPanTranslationOffset;
        private Paint backgroundPaint = new Paint();
        private int backgroundColor;

        public LayoutContainer(Context context) {
            super(context);
            setWillNotDraw(false);
        }

        @Override
        protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
            if (child instanceof Toolbar) {
                return super.drawChild(canvas, child, drawingTime);
            } else {
                int toolbarHeight = 0;
                int toolbarY = 0;
                int childCount = getChildCount();
                for (int a = 0; a < childCount; a++) {
                    View view = getChildAt(a);
                    if (view == child) {
                        continue;
                    }
                    if (view instanceof Toolbar && view.getVisibility() == VISIBLE) {
                        if (((Toolbar) view).getCastShadows()) {
                            toolbarHeight = view.getMeasuredHeight();
                            toolbarY = (int) view.getY();
                        }
                        break;
                    }
                }
                boolean result = super.drawChild(canvas, child, drawingTime);
                if (toolbarHeight != 0 && headerShadowDrawable != null) {
                    headerShadowDrawable.setBounds(0, toolbarY + toolbarHeight, getMeasuredWidth(), toolbarY + toolbarHeight + headerShadowDrawable.getIntrinsicHeight());
                    headerShadowDrawable.draw(canvas);
                }
                return result;
            }
        }

        @Override
        public boolean hasOverlappingRendering() {
            if (Build.VERSION.SDK_INT >= 28) {
                return true;
            }
            return false;
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int width = MeasureSpec.getSize(widthMeasureSpec);
            int height = MeasureSpec.getSize(heightMeasureSpec);
            int count = getChildCount();
            int toolbarHeight = 0;
            for (int a = 0; a < count; a++) {
                View child = getChildAt(a);
                if (child instanceof Toolbar) {
                    child.measure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(height, MeasureSpec.UNSPECIFIED));
                    toolbarHeight = child.getMeasuredHeight();
                    break;
                }
            }
            for (int a = 0; a < count; a++) {
                View child = getChildAt(a);
                if (!(child instanceof Toolbar)) {
                    if (child.getFitsSystemWindows()) {
                        measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, 0);
                    } else {
                        measureChildWithMargins(child, widthMeasureSpec, 0, heightMeasureSpec, toolbarHeight);
                    }
                }
            }
            setMeasuredDimension(width, height);
        }

        @Override
        protected void onLayout(boolean changed, int l, int t, int r, int b) {
            int count = getChildCount();
            int toolbarHeight = 0;
            for (int a = 0; a < count; a++) {
                View child = getChildAt(a);
                if (child instanceof Toolbar) {
                    toolbarHeight = child.getMeasuredHeight();
                    child.layout(0, 0, child.getMeasuredWidth(), toolbarHeight);
                    break;
                }
            }
            for (int a = 0; a < count; a++) {
                View child = getChildAt(a);
                if (!(child instanceof Toolbar)) {
                    FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) child.getLayoutParams();
                    if (child.getFitsSystemWindows()) {
                        child.layout(layoutParams.leftMargin, layoutParams.topMargin, layoutParams.leftMargin + child.getMeasuredWidth(), layoutParams.topMargin + child.getMeasuredHeight());
                    } else {
                        child.layout(layoutParams.leftMargin, layoutParams.topMargin + toolbarHeight, layoutParams.leftMargin + child.getMeasuredWidth(), layoutParams.topMargin + toolbarHeight + child.getMeasuredHeight());
                    }
                }
            }

            View rootView = getRootView();
            getWindowVisibleDisplayFrame(rect);
            int usableViewHeight = rootView.getHeight() - (rect.top != 0 ? statusBarHeight : 0) - getViewInset(rootView);
            isKeyboardVisible = usableViewHeight - (rect.bottom - rect.top) > 0;
            if (waitingForKeyboardCloseRunnable != null && !containerView.isKeyboardVisible && !containerViewBack.isKeyboardVisible) {
                G.cancelRunOnUiThread(waitingForKeyboardCloseRunnable);
                waitingForKeyboardCloseRunnable.run();
                waitingForKeyboardCloseRunnable = null;
            }
        }

        @Override
        public boolean dispatchTouchEvent(MotionEvent ev) {
            if ((inPreviewMode) && (ev.getActionMasked() == MotionEvent.ACTION_DOWN || ev.getActionMasked() == MotionEvent.ACTION_POINTER_DOWN)) {
                return false;
            }
            //
            try {
                return (!inPreviewMode || this != containerView) && super.dispatchTouchEvent(ev);
            } catch (Throwable e) {
                FileLog.e(e);
            }
            return false;
        }

        @Override
        protected void onDraw(Canvas canvas) {
            if (fragmentPanTranslationOffset != 0) {
                int color = Theme.getColor(Theme.key_window_background);
                if (backgroundColor != color) {
                    backgroundPaint.setColor(backgroundColor = Theme.getColor(Theme.key_window_background));
                }
                canvas.drawRect(0, getMeasuredHeight() - fragmentPanTranslationOffset - 3, getMeasuredWidth(), getMeasuredHeight(), backgroundPaint);
            }
            super.onDraw(canvas);
        }

        public void setFragmentPanTranslationOffset(int fragmentPanTranslationOffset) {
            this.fragmentPanTranslationOffset = fragmentPanTranslationOffset;
            invalidate();
        }

        @Override
        public void setTranslationX(float translationX) {
            Log.d("kek", "set translationX" + translationX);
            super.setTranslationX(translationX);
        }
    }

    private static Drawable headerShadowDrawable;
    private static Drawable layerShadowDrawable;
    private static Paint scrimPaint;

    private Runnable waitingForKeyboardCloseRunnable;
    private Runnable delayedOpenAnimationRunnable;

    private boolean inPreviewMode;
    private boolean previewOpenAnimationInProgress;

    private LayoutContainer containerView;
    private LayoutContainer containerViewBack;
    private Toolbar currentToolBar;

    private BaseFragment oldFragment;

    public float innerTranslationX;

    private boolean maybeStartTracking;
    protected boolean startedTracking;
    private int startedTrackingX;
    private int startedTrackingY;
    protected boolean animationInProgress;
    private VelocityTracker velocityTracker;
    private View layoutToIgnore;
    private boolean beginTrackingSent;

    private int startedTrackingPointerId;

    private ToolBarLayoutDelegate delegate;
    public Activity parentActivity;

    public ArrayList<BaseFragment> fragmentsStack;
    private Rect rect = new Rect();

    public void init(ArrayList<BaseFragment> stack) {
        fragmentsStack = stack;
        containerViewBack = new LayoutContainer(parentActivity);
        addView(containerViewBack);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) containerViewBack.getLayoutParams();
        layoutParams.width = LayoutCreator.MATCH_PARENT;
        layoutParams.height = LayoutCreator.MATCH_PARENT;
        layoutParams.gravity = Gravity.TOP | Gravity.LEFT;
        containerViewBack.setLayoutParams(layoutParams);

        containerView = new LayoutContainer(parentActivity);
        addView(containerView);
        layoutParams = (FrameLayout.LayoutParams) containerView.getLayoutParams();
        layoutParams.width = LayoutCreator.MATCH_PARENT;
        layoutParams.height = LayoutCreator.MATCH_PARENT;
        layoutParams.gravity = Gravity.TOP | Gravity.LEFT;
        containerView.setLayoutParams(layoutParams);

        for (BaseFragment fragment : fragmentsStack) {
            fragment.setParentLayout(this);
        }
    }

    @Override
    public void onConfigurationChanged(android.content.res.Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (!fragmentsStack.isEmpty()) {
            for (int a = 0, N = fragmentsStack.size(); a < N; a++) {
                BaseFragment fragment = fragmentsStack.get(a);
                fragment.onConfigurationChanged(newConfig);
            }
        }
    }

    @Keep
    public void setInnerTranslationX(float value) {
        innerTranslationX = value;
        invalidate();

        if (fragmentsStack.size() >= 2 && containerView.getMeasuredWidth() > 0) {
            BaseFragment prevFragment = fragmentsStack.get(fragmentsStack.size() - 2);
        }
    }

    public void onResume() {
        if (!fragmentsStack.isEmpty()) {
            BaseFragment lastFragment = fragmentsStack.get(fragmentsStack.size() - 1);
            lastFragment.onResume();
        }
    }

    public void onPause() {
        if (!fragmentsStack.isEmpty()) {
            BaseFragment lastFragment = fragmentsStack.get(fragmentsStack.size() - 1);
            lastFragment.onPause();
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return onTouchEvent(ev);
    }

    @Override
    public void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        onTouchEvent(null);
        super.requestDisallowInterceptTouchEvent(disallowIntercept);
    }

    @Override
    public boolean dispatchKeyEventPreIme(KeyEvent event) {
        if (event != null && event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
            return delegate != null && delegate.onPreIme() || super.dispatchKeyEventPreIme(event);
        }
        return super.dispatchKeyEventPreIme(event);
    }

    @Override
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        int width = getWidth() - getPaddingLeft() - getPaddingRight();
        int translationX = (int) innerTranslationX + getPaddingRight();
        int clipLeft = getPaddingLeft();
        int clipRight = width + getPaddingLeft();

        if (child == containerViewBack) {
            clipRight = translationX + LayoutCreator.dp(1);
        } else if (child == containerView) {
            clipLeft = translationX;
        }

        final int restoreCount = canvas.save();
        if (!inPreviewMode) {
            canvas.clipRect(clipLeft, 0, clipRight, getHeight());
        }
        if ((inPreviewMode) && child == containerView) {
            drawPreviewDrawables(canvas, containerView);
        }
        final boolean result = super.drawChild(canvas, child, drawingTime);
        canvas.restoreToCount(restoreCount);

        if (translationX != 0) {
            if (child == containerView) {
                final float alpha = Math.max(0, Math.min((width - translationX) / (float) LayoutCreator.dp(20), 1.0f));
                layerShadowDrawable.setBounds(translationX - layerShadowDrawable.getIntrinsicWidth(), child.getTop(), translationX, child.getBottom());
                layerShadowDrawable.setAlpha((int) (0xff * alpha));
                layerShadowDrawable.draw(canvas);
            } else if (child == containerViewBack) {
                float opacity = Math.min(0.8f, (width - translationX) / (float)width);
                if (opacity < 0) {
                    opacity = 0;
                }
                scrimPaint.setColor((int) (((0x99000000 & 0xff000000) >>> 24) * opacity) << 24);
                canvas.drawRect(clipLeft, 0, clipRight, getHeight(), scrimPaint);
            }
        }

        return result;
    }

    public float getCurrentPreviewFragmentAlpha() {
        if (inPreviewMode || previewOpenAnimationInProgress) {
            return (oldFragment != null && oldFragment.inPreviewMode ? containerViewBack : containerView).getAlpha();
        } else {
            return 0f;
        }
    }

    public void drawCurrentPreviewFragment(Canvas canvas, Drawable foregroundDrawable) {
        if (inPreviewMode || previewOpenAnimationInProgress) {
            final ViewGroup v = oldFragment != null && oldFragment.inPreviewMode ? containerViewBack : containerView;
            drawPreviewDrawables(canvas, v);
            if (v.getAlpha() < 1f) {
                canvas.saveLayerAlpha(0, 0, getWidth(), getHeight(), (int) (v.getAlpha() * 255), Canvas.ALL_SAVE_FLAG);
            } else {
                canvas.save();
            }
            canvas.concat(v.getMatrix());
            v.draw(canvas);
            if (foregroundDrawable != null) {
                final View child = v.getChildAt(0);
                if (child != null) {
                    final MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
                    final Rect rect = new Rect();
                    child.getLocalVisibleRect(rect);
                    rect.offset(lp.leftMargin, lp.topMargin);
                    rect.top += Build.VERSION.SDK_INT >= 21 ? statusBarHeight - 1 : 0;
                    foregroundDrawable.setAlpha((int) (v.getAlpha() * 255));
                    foregroundDrawable.setBounds(rect);
                    foregroundDrawable.draw(canvas);
                }
            }
            canvas.restore();
        }
    }

    private void drawPreviewDrawables(Canvas canvas, ViewGroup containerView) {
        View view = containerView.getChildAt(0);
        if (view != null) {
            int x = (getMeasuredWidth() - LayoutCreator.dp(24)) / 2;
            int y = (int) (view.getTop() + containerView.getTranslationY() - LayoutCreator.dp(12 + (Build.VERSION.SDK_INT < 21 ? 20 : 0)));
        }
    }

    public void setDelegate(ToolBarLayoutDelegate toolbarLayoutDelegate) {
        delegate = toolbarLayoutDelegate;
    }

    private void onSlideAnimationEnd(final boolean backAnimation) {
        if (!backAnimation) {
            if (fragmentsStack.size() < 2) {
                return;
            }
            BaseFragment lastFragment = fragmentsStack.get(fragmentsStack.size() - 1);
            lastFragment.onPause();
            lastFragment.setParentLayout(null);

            fragmentsStack.remove(fragmentsStack.size() - 1);

            LayoutContainer temp = containerView;
            containerView = containerViewBack;
            containerViewBack = temp;
            bringChildToFront(containerView);

            lastFragment = fragmentsStack.get(fragmentsStack.size() - 1);
            currentToolBar = lastFragment.toolbar;
            lastFragment.onResume();
            lastFragment.onBecomeFullyVisible();

            layoutToIgnore = containerView;
        } else {
            if (fragmentsStack.size() >= 2) {
                BaseFragment lastFragment = fragmentsStack.get(fragmentsStack.size() - 1);

                lastFragment = fragmentsStack.get(fragmentsStack.size() - 2);
                lastFragment.onPause();
                if (lastFragment.fragmentView != null) {
                    ViewGroup parent = (ViewGroup) lastFragment.fragmentView.getParent();
                    if (parent != null) {
                        parent.removeViewInLayout(lastFragment.fragmentView);
                    }
                }
                if (lastFragment.toolbar != null && lastFragment.toolbar.shouldAddToContainer()) {
                    ViewGroup parent = (ViewGroup) lastFragment.toolbar.getParent();
                    if (parent != null) {
                        parent.removeViewInLayout(lastFragment.toolbar);
                    }
                }
            }
            layoutToIgnore = null;
        }
        containerViewBack.setVisibility(View.INVISIBLE);
        startedTracking = false;
        animationInProgress = false;
        containerView.setTranslationX(0);
        containerViewBack.setTranslationX(0);
        setInnerTranslationX(0);
    }

    private void prepareForMoving(MotionEvent ev) {
        maybeStartTracking = false;
        startedTracking = true;
        layoutToIgnore = containerViewBack;
        startedTrackingX = (int) ev.getX();
        containerViewBack.setVisibility(View.VISIBLE);
        beginTrackingSent = false;

        BaseFragment lastFragment = fragmentsStack.get(fragmentsStack.size() - 2);
        View fragmentView = lastFragment.fragmentView;
        if (fragmentView == null) {
            fragmentView = lastFragment.createView(parentActivity);
        }
        ViewGroup parent = (ViewGroup) fragmentView.getParent();
        if (parent != null) {
            parent.removeView(fragmentView);
        }
        containerViewBack.addView(fragmentView);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) fragmentView.getLayoutParams();
        layoutParams.width = LayoutCreator.MATCH_PARENT;
        layoutParams.height = LayoutCreator.MATCH_PARENT;
        layoutParams.topMargin = layoutParams.bottomMargin = layoutParams.rightMargin = layoutParams.leftMargin = 0;
        fragmentView.setLayoutParams(layoutParams);
        if (lastFragment.toolbar != null && lastFragment.toolbar.shouldAddToContainer()) {
            parent = (ViewGroup) lastFragment.toolbar.getParent();
            if (parent != null) {
                parent.removeView(lastFragment.toolbar);
            }
            containerViewBack.addView(lastFragment.toolbar);
        }
        if (fragmentView.getBackground() == null) {
            fragmentView.setBackgroundColor(Theme.getColor(Theme.key_window_background));
        }
        lastFragment.onResume();

        BaseFragment currentFragment = fragmentsStack.get(fragmentsStack.size() - 1);
    }

    public boolean onTouchEvent(MotionEvent ev) {
        if (fragmentsStack.size() > 1) {
            if (ev != null && ev.getAction() == MotionEvent.ACTION_DOWN) {
                BaseFragment currentFragment = fragmentsStack.get(fragmentsStack.size() - 1);
                if (!currentFragment.isSwipeBackEnabled(ev)) {
                    maybeStartTracking = false;
                    startedTracking = false;
                    return false;
                }
                startedTrackingPointerId = ev.getPointerId(0);
                maybeStartTracking = true;
                startedTrackingX = (int) ev.getX();
                startedTrackingY = (int) ev.getY();
                if (velocityTracker != null) {
                    velocityTracker.clear();
                }
            } else if (ev != null && ev.getAction() == MotionEvent.ACTION_MOVE && ev.getPointerId(0) == startedTrackingPointerId) {
                if (velocityTracker == null) {
                    velocityTracker = VelocityTracker.obtain();
                }
                int dx = Math.max(0, (int) (ev.getX() - startedTrackingX));
                int dy = Math.abs((int) ev.getY() - startedTrackingY);
                velocityTracker.addMovement(ev);
                if (!inPreviewMode && maybeStartTracking && !startedTracking && dx >= getPixelsInCM(0.4f, true) && Math.abs(dx) / 3 > dy) {
                    BaseFragment currentFragment = fragmentsStack.get(fragmentsStack.size() - 1);
                    if (currentFragment.canBeginSlide() && findScrollingChild(this, ev.getX(), ev.getY()) == null) {
                        prepareForMoving(ev);
                    } else {
                        maybeStartTracking = false;
                    }
                } else if (startedTracking) {
                    if (!beginTrackingSent) {
                        if (parentActivity.getCurrentFocus() != null) {
                            hideKeyboard(parentActivity.getCurrentFocus());
                        }
                        BaseFragment currentFragment = fragmentsStack.get(fragmentsStack.size() - 1);
                        currentFragment.onBeginSlide();
                        beginTrackingSent = true;
                    }
                    containerView.setTranslationX(dx);
                    setInnerTranslationX(dx);
                }
            } else if (ev != null && ev.getPointerId(0) == startedTrackingPointerId && (ev.getAction() == MotionEvent.ACTION_CANCEL || ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_POINTER_UP)) {
                if (velocityTracker == null) {
                    velocityTracker = VelocityTracker.obtain();
                }
                velocityTracker.computeCurrentVelocity(1000);
                BaseFragment currentFragment = fragmentsStack.get(fragmentsStack.size() - 1);
                if (!inPreviewMode && !startedTracking && currentFragment.isSwipeBackEnabled(ev)) {
                    float velX = velocityTracker.getXVelocity();
                    float velY = velocityTracker.getYVelocity();
                    if (velX >= 3500 && velX > Math.abs(velY) && currentFragment.canBeginSlide()) {
                        prepareForMoving(ev);
                        if (!beginTrackingSent) {
                            if (((Activity) getContext()).getCurrentFocus() != null) {
                                hideKeyboard(((Activity) getContext()).getCurrentFocus());
                            }
                            beginTrackingSent = true;
                        }
                    }
                }
                if (startedTracking) {
                    float x = containerView.getX();
                    AnimatorSet animatorSet = new AnimatorSet();
                    float velX = velocityTracker.getXVelocity();
                    float velY = velocityTracker.getYVelocity();
                    final boolean backAnimation = x < containerView.getMeasuredWidth() / 3.0f && (velX < 3500 || velX < velY);
                    float distToMove;
                    if (!backAnimation) {
                        distToMove = containerView.getMeasuredWidth() - x;
                        int duration = Math.max((int) (200.0f / containerView.getMeasuredWidth() * distToMove), 50);
                        animatorSet.playTogether(
                                ObjectAnimator.ofFloat(containerView, View.TRANSLATION_X, containerView.getMeasuredWidth()).setDuration(duration),
                                ObjectAnimator.ofFloat(this, "innerTranslationX", (float) containerView.getMeasuredWidth()).setDuration(duration)
                        );
                    } else {
                        distToMove = x;
                        int duration = Math.max((int) (200.0f / containerView.getMeasuredWidth() * distToMove), 50);
                        animatorSet.playTogether(
                                ObjectAnimator.ofFloat(containerView, View.TRANSLATION_X, 0).setDuration(duration),
                                ObjectAnimator.ofFloat(this, "innerTranslationX", 0.0f).setDuration(duration)
                        );
                    }

                    BaseFragment lastFragment = fragmentsStack.get(fragmentsStack.size() - 2);

                    animatorSet.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animator) {
                            onSlideAnimationEnd(backAnimation);
                        }
                    });
                    animatorSet.start();
                    animationInProgress = true;
                    layoutToIgnore = containerViewBack;
                } else {
                    maybeStartTracking = false;
                    startedTracking = false;
                    layoutToIgnore = null;
                }
                if (velocityTracker != null) {
                    velocityTracker.recycle();
                    velocityTracker = null;
                }
            } else if (ev == null) {
                maybeStartTracking = false;
                startedTracking = false;
                layoutToIgnore = null;
                if (velocityTracker != null) {
                    velocityTracker.recycle();
                    velocityTracker = null;
                }
            }
        }
        return startedTracking;
    }

    public void onBackPressed() {
        if (startedTracking || fragmentsStack.isEmpty()) {
            return;
        }

        BaseFragment lastFragment = fragmentsStack.get(fragmentsStack.size() - 1);
        if (lastFragment.onBackPressed()) {
            if (!fragmentsStack.isEmpty()) {
                closeLastFragment(true);
            }
        }
    }

    private void presentFragmentInternalRemoveOld(boolean removeLast, final BaseFragment fragment) {
        if (fragment == null) {
            return;
        }
        fragment.onPause();
        if (removeLast) {
            fragment.setParentLayout(null);
            fragmentsStack.remove(fragment);
        } else {
            if (fragment.fragmentView != null) {
                ViewGroup parent = (ViewGroup) fragment.fragmentView.getParent();
                if (parent != null) {
                    try {
                        parent.removeViewInLayout(fragment.fragmentView);
                    } catch (Exception e) {
                        FileLog.e(e);
                        try {
                            parent.removeView(fragment.fragmentView);
                        } catch (Exception e2) {
                            FileLog.e(e2);
                        }
                    }
                }
            }
            if (fragment.toolbar != null && fragment.toolbar.shouldAddToContainer()) {
                ViewGroup parent = (ViewGroup) fragment.toolbar.getParent();
                if (parent != null) {
                    parent.removeViewInLayout(fragment.toolbar);
                }
            }
        }
        containerViewBack.setVisibility(View.INVISIBLE);
    }

    public void movePreviewFragment(float dy) {
        if (!inPreviewMode) {
            return;
        }
        float currentTranslation = containerView.getTranslationY();
        float nextTranslation = -dy;
        if (nextTranslation > 0) {
            nextTranslation = 0;
        } else if (nextTranslation < -LayoutCreator.dp(60)) {
            previewOpenAnimationInProgress = true;
            inPreviewMode = false;
            nextTranslation = 0;

            BaseFragment prevFragment = fragmentsStack.get(fragmentsStack.size() - 2);
            BaseFragment fragment = fragmentsStack.get(fragmentsStack.size() - 1);

            if (Build.VERSION.SDK_INT >= 21) {
                fragment.fragmentView.setOutlineProvider(null);
                fragment.fragmentView.setClipToOutline(false);
            }
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) fragment.fragmentView.getLayoutParams();
            layoutParams.topMargin = layoutParams.bottomMargin = layoutParams.rightMargin = layoutParams.leftMargin = 0;
            layoutParams.height = LayoutCreator.MATCH_PARENT;
            fragment.fragmentView.setLayoutParams(layoutParams);

            presentFragmentInternalRemoveOld(false, prevFragment);

            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(
                    ObjectAnimator.ofFloat(fragment.fragmentView, View.SCALE_X, 1.0f, 1.05f, 1.0f),
                    ObjectAnimator.ofFloat(fragment.fragmentView, View.SCALE_Y, 1.0f, 1.05f, 1.0f));
            animatorSet.setDuration(200);
            animatorSet.setInterpolator(new CubicBezierInterpolator(0.42, 0.0, 0.58, 1.0));
            animatorSet.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    previewOpenAnimationInProgress = false;
                }
            });
            animatorSet.start();
            performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);

            fragment.setInPreviewMode(false);
        }
        if (currentTranslation != nextTranslation) {
            containerView.setTranslationY(nextTranslation);
            invalidate();
        }
    }

    public void finishPreviewFragment() {
        if (!inPreviewMode) {
            return;
        }
        if (delayedOpenAnimationRunnable != null) {
            G.cancelRunOnUiThread(delayedOpenAnimationRunnable);
            delayedOpenAnimationRunnable = null;
        }
        closeLastFragment(true);
    }

    public void closeLastFragment(boolean animated) {
        if (delegate != null && !delegate.needCloseLastFragment(this) || fragmentsStack.isEmpty()) {
            return;
        }
        if (parentActivity.getCurrentFocus() != null) {
            hideKeyboard(parentActivity.getCurrentFocus());
        }
        setInnerTranslationX(0);
        final BaseFragment currentFragment = fragmentsStack.get(fragmentsStack.size() - 1);
        BaseFragment previousFragment = null;
        if (fragmentsStack.size() > 1) {
            previousFragment = fragmentsStack.get(fragmentsStack.size() - 2);
        }

        if (previousFragment != null) {
            LayoutContainer temp = containerView;
            containerView = containerViewBack;
            containerViewBack = temp;

            previousFragment.setParentLayout(this);
            View fragmentView = previousFragment.fragmentView;
            if (fragmentView == null) {
                fragmentView = previousFragment.createView(parentActivity);
            }

            if (!inPreviewMode) {
                containerView.setVisibility(View.VISIBLE);
                ViewGroup parent = (ViewGroup) fragmentView.getParent();
                if (parent != null) {
                    try {
                        parent.removeView(fragmentView);
                    } catch (Exception e) {
                        FileLog.e(e);
                    }
                }
                containerView.addView(fragmentView);
                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) fragmentView.getLayoutParams();
                layoutParams.width = LayoutCreator.MATCH_PARENT;
                layoutParams.height = LayoutCreator.MATCH_PARENT;
                layoutParams.topMargin = layoutParams.bottomMargin = layoutParams.rightMargin = layoutParams.leftMargin = 0;
                fragmentView.setLayoutParams(layoutParams);
                if (previousFragment.toolbar != null && previousFragment.toolbar.shouldAddToContainer()) {
                    parent = (ViewGroup) previousFragment.toolbar.getParent();
                    if (parent != null) {
                        parent.removeView(previousFragment.toolbar);
                    }
                    containerView.addView(previousFragment.toolbar);
                }
            }

            oldFragment = currentFragment;
            previousFragment.onResume();
            currentToolBar = previousFragment.toolbar;
            previousFragment.onBecomeFullyVisible();

        } else {
            removeFragmentFromStackInternal(currentFragment);
            setVisibility(GONE);
        }
    }

    public void showLastFragment() {
        if (fragmentsStack.isEmpty()) {
            return;
        }
        for (int a = 0; a < fragmentsStack.size() - 1; a++) {
            BaseFragment previousFragment = fragmentsStack.get(a);
            if (previousFragment.toolbar != null && previousFragment.toolbar.shouldAddToContainer()) {
                ViewGroup parent = (ViewGroup) previousFragment.toolbar.getParent();
                if (parent != null) {
                    parent.removeView(previousFragment.toolbar);
                }
            }
            if (previousFragment.fragmentView != null) {
                ViewGroup parent = (ViewGroup) previousFragment.fragmentView.getParent();
                if (parent != null) {
                    previousFragment.onPause();
                    parent.removeView(previousFragment.fragmentView);
                }
            }
        }
        BaseFragment previousFragment = fragmentsStack.get(fragmentsStack.size() - 1);
        previousFragment.setParentLayout(this);
        View fragmentView = previousFragment.fragmentView;
        if (fragmentView == null) {
            fragmentView = previousFragment.createView(parentActivity);
        } else {
            ViewGroup parent = (ViewGroup) fragmentView.getParent();
            if (parent != null) {
                parent.removeView(fragmentView);
            }
        }
        containerView.addView(fragmentView, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.MATCH_PARENT));
        if (previousFragment.toolbar != null && previousFragment.toolbar.shouldAddToContainer()) {
            ViewGroup parent = (ViewGroup) previousFragment.toolbar.getParent();
            if (parent != null) {
                parent.removeView(previousFragment.toolbar);
            }
            containerView.addView(previousFragment.toolbar);
        }
        previousFragment.onResume();
        currentToolBar = previousFragment.toolbar;
    }

    private void removeFragmentFromStackInternal(BaseFragment fragment) {
        fragment.onPause();
        fragment.setParentLayout(null);
        fragmentsStack.remove(fragment);
    }

    public void rebuildAllFragmentViews(boolean last, boolean showLastAfter) {
        int size = fragmentsStack.size();
        if (!last) {
            size--;
        }
        if (inPreviewMode) {
            size--;
        }
        for (int a = 0; a < size; a++) {
            fragmentsStack.get(a).setParentLayout(this);
        }
        if (delegate != null) {
            delegate.onRebuildAllFragments(this, last);
        }
        if (showLastAfter) {
            showLastFragment();
        }
    }

    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU && !startedTracking && currentToolBar != null) {
            currentToolBar.onMenuButtonPressed();
        }
        return super.onKeyUp(keyCode, event);
    }

    public void startActivityForResult(final Intent intent, final int requestCode) {
        if (parentActivity == null) {
            return;
        }
        if (intent != null) {
            parentActivity.startActivityForResult(intent, requestCode);
        }
    }

    @Override
    public boolean hasOverlappingRendering() {
        return false;
    }

    private View findScrollingChild(ViewGroup parent, float x, float y) {
        int n = parent.getChildCount();
        for (int i = 0; i < n; i++) {
            View child = parent.getChildAt(i);
            if (child.getVisibility() != View.VISIBLE) {
                continue;
            }
            child.getHitRect(rect);
            if (rect.contains((int) x, (int) y)) {
                if (child.canScrollHorizontally(-1)) {
                    return child;
                } else if (child instanceof ViewGroup) {
                    View v = findScrollingChild((ViewGroup) child, x - rect.left, y - rect.top);
                    if (v != null) {
                        return v;
                    }
                }
            }
        }
        return null;
    }
}
