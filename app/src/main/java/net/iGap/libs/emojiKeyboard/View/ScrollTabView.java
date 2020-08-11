package net.iGap.libs.emojiKeyboard.View;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import net.iGap.fragments.emoji.struct.StructIGStickerGroup;
import net.iGap.helper.LayoutCreator;
import net.iGap.module.Theme;
import net.iGap.module.customView.StickerView;

public class ScrollTabView extends HorizontalScrollView {


    private LinearLayout.LayoutParams defaultTabLayoutParams;
    private LinearLayout.LayoutParams defaultExpandLayoutParams;
    private LinearLayout rootView;
    private Listener listener;

    private int tabCount;
    private int currentPosition;
    private boolean animateFromPosition;
    private float startAnimationPosition;
    private float positionAnimationProgress;
    private long lastAnimationTime;
    private int indicatorColor;
    private int indicatorHeight;
    private int lastScrollX = 0;
    private boolean shouldExpand;

    private Paint paint;

    private int scrollOffset = LayoutCreator.dp(52);
    private int rectRound = LayoutCreator.dp(2);

    private String TAG = "abbasiEmoji";

    public interface Listener {
        void onPageSelected(int page);
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public ScrollTabView(Context context) {
        super(context);


        setFillViewport(true);
        setWillNotDraw(false);

        setHorizontalScrollBarEnabled(false);
        rootView = new LinearLayout(context);
        rootView.setOrientation(LinearLayout.HORIZONTAL);
        rootView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutCreator.MATCH_PARENT));
        addView(rootView);

        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);

        defaultExpandLayoutParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1.0F);
        defaultTabLayoutParams = new LinearLayout.LayoutParams(LayoutCreator.dp(40), LayoutCreator.MATCH_PARENT);
    }

    public void updateTabStyles() {
        for (int i = 0; i < tabCount; i++) {
            View v = rootView.getChildAt(i);
            if (shouldExpand) {
                v.setLayoutParams(defaultExpandLayoutParams);
            } else {
                v.setLayoutParams(defaultTabLayoutParams);
            }
        }
    }

    public void removeTabs() {
        rootView.removeAllViews();
        tabCount = 0;
        currentPosition = 0;
        animateFromPosition = false;
    }

    public void selectTab(int num) {
        if (num < 0 || num >= tabCount) {
            return;
        }
        View tab = rootView.getChildAt(num);
        tab.performClick();
    }

    public void addStickerTab(StructIGStickerGroup sticker) {
        final int position = tabCount++;
        FrameLayout tab = new FrameLayout(getContext());
        tab.setFocusable(true);
        tab.setOnClickListener(v -> listener.onPageSelected(position));
        rootView.addView(tab);
        tab.setSelected(position == currentPosition);

        StickerView stickerView = new StickerView(getContext());
        stickerView.loadStickerGroup(sticker);

        tab.addView(stickerView, LayoutCreator.createFrame(30, 30, Gravity.CENTER));

    }

    private void scrollToChild(int position) {
        if (tabCount == 0 || rootView.getChildAt(position) == null) {
            return;
        }
        int newScrollX = rootView.getChildAt(position).getLeft();
        if (position > 0) {
            newScrollX -= scrollOffset;
        }
        int currentScrollX = getScrollX();
        if (newScrollX != lastScrollX) {
            if (newScrollX < currentScrollX) {
                lastScrollX = newScrollX;
                smoothScrollTo(lastScrollX, 0);
            } else if (newScrollX + scrollOffset > currentScrollX + getWidth() - scrollOffset * 2) {
                lastScrollX = newScrollX - getWidth() + scrollOffset * 3;
                smoothScrollTo(lastScrollX, 0);
            }
        }
    }

    public void addIconTab(Drawable drawable) {
        final int position = tabCount++;
        ImageView imageView = new ImageView(getContext());
        imageView.setFocusable(true);
        imageView.setImageDrawable(drawable);
        imageView.setColorFilter(Theme.getInstance().getTitleTextColor(getContext()));
        imageView.setScaleType(ImageView.ScaleType.CENTER);
        imageView.setOnClickListener(v -> listener.onPageSelected(position));
        rootView.addView(imageView);
        imageView.setSelected(position == currentPosition);
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (isInEditMode() || tabCount == 0) {
            return;
        }

        final int height = getHeight();

        if (indicatorHeight >= 0) {
            View currentTab = rootView.getChildAt(currentPosition);
            float lineLeft = 0;
            int width = 0;
            if (currentTab != null) {
                lineLeft = currentTab.getLeft();
                width = currentTab.getMeasuredWidth();
            }
            if (animateFromPosition) {
                long newTime = SystemClock.uptimeMillis();
                long dt = newTime - lastAnimationTime;
                lastAnimationTime = newTime;

                positionAnimationProgress += dt / 150.0f;
                if (positionAnimationProgress >= 1.0f) {
                    positionAnimationProgress = 1.0f;
                    animateFromPosition = false;
                }
                lineLeft = startAnimationPosition + (lineLeft - startAnimationPosition) * CubicBezierInterpolator.EASE_OUT_QUINT.getInterpolation(positionAnimationProgress);
                invalidate();
            }

            paint.setColor(indicatorColor);
            if (indicatorHeight == 0) {
                canvas.drawRoundRect(new RectF(lineLeft, 0, lineLeft + width, height), rectRound, rectRound, paint);
            } else {
                canvas.drawRoundRect(new RectF(lineLeft, height - indicatorHeight, lineLeft + width, height), rectRound, rectRound, paint);
            }
        }
    }

    public int getCurrentPosition() {
        return currentPosition;
    }

    public void cancelPositionAnimation() {
        animateFromPosition = false;
        positionAnimationProgress = 1.0f;
    }

    public void onPageScrolled(int position, int first) {
        if (currentPosition == position) {
            return;
        }

        View currentTab = rootView.getChildAt(currentPosition);
        if (currentTab != null) {
            startAnimationPosition = currentTab.getLeft();
            positionAnimationProgress = 0.0f;
            animateFromPosition = true;
            lastAnimationTime = SystemClock.uptimeMillis();
        } else {
            animateFromPosition = false;
        }
        currentPosition = position;
        if (position >= rootView.getChildCount()) {
            return;
        }
        positionAnimationProgress = 0.0f;
        for (int a = 0; a < rootView.getChildCount(); a++) {
            rootView.getChildAt(a).setSelected(a == position);
        }
        if (first == position && position > 1) {
            scrollToChild(position - 1);
        } else {
            scrollToChild(position);
        }
        invalidate();
    }

    public void setIndicatorHeight(int value) {
        indicatorHeight = value;
        invalidate();
    }

    public void setIndicatorColor(int value) {
        indicatorColor = value;
        invalidate();
    }

    public void setShouldExpand(boolean value) {
        shouldExpand = value;
        requestLayout();
    }
}
