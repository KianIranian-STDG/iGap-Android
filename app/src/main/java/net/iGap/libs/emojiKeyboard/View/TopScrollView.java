package net.iGap.libs.emojiKeyboard.View;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.SystemClock;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;

import net.iGap.G;
import net.iGap.R;
import net.iGap.helper.LayoutCreator;
import net.iGap.messenger.theme.Theme;

import java.util.ArrayList;
import java.util.List;

public class TopScrollView extends HorizontalScrollView {
    private List<FrameLayout> childList = new ArrayList<>();
    private LinearLayout root;
    private Listener listener;
    private Paint paint;


    private int tabCount;
    private int currentPosition;
    private float startAnimationP;
    private float animationDuration;
    private long lastAnimationTime;
    private int lastScrollX = 0;
    private int lastTextViewIndex = 999;
    private final int scrollOffset = LayoutCreator.dp(120);
    private boolean animateFromPosition;
    private final int indicatorColor = Theme.getColor(Theme.key_window_background);
    private final float indicatorHeight = LayoutCreator.dp(5);


    public TopScrollView(Context context) {
        super(context);


        root = new LinearLayout(context);
        root.setOrientation(LinearLayout.HORIZONTAL);
        root.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutCreator.MATCH_PARENT));

        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);

        setFillViewport(true);
        setWillNotDraw(false);
        setHorizontalScrollBarEnabled(false);
        addView(root);
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public void addTab(String tabTitle) {
        tabCount++;
        FrameLayout tabContainer = new FrameLayout(getContext());
        tabContainer.setLayoutParams(LayoutCreator.createLinear(LayoutCreator.WRAP_CONTENT, LayoutCreator.MATCH_PARENT));
        TextView tabContent = new TextView(getContext());
        tabContent.setLayoutParams(LayoutCreator.createFrame(LayoutCreator.WRAP_CONTENT, LayoutCreator.WRAP_CONTENT, Gravity.CENTER, 32, 7, 32, 7));

        tabContent.setText(tabTitle);
        tabContainer.setFocusable(true);
        tabContent.setGravity(Gravity.CENTER);
        tabContent.setTypeface(ResourcesCompat.getFont(G.context, R.font.main_font_bold));
        tabContent.setTextColor(Theme.getColor(Theme.key_default_text));
        tabContent.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        tabContent.setGravity(Gravity.CENTER);

        tabContainer.addView(tabContent);
        childList.add(tabContainer);
        root.addView(tabContainer);


        tabContainer.setOnClickListener(v -> {
            int index = childList.indexOf(tabContainer);
            listener.onTabSelected(index);
        });
    }

    public void changeItemStyle(int index) {
        if (lastTextViewIndex != 999) {
            TextView lastTabContent = (TextView) childList.get(lastTextViewIndex).getChildAt(0);
            lastTabContent.setTextColor(Theme.getColor(Theme.key_default_text));
        }
        TextView tabContent = (TextView) childList.get(index).getChildAt(0);
        tabContent.setTextColor(Theme.getColor(Theme.key_light_theme_color));
        lastTextViewIndex = index;
    }

    public void scrollToChild(int position) {
        if (tabCount == 0 || root.getChildAt(position) == null) {
            return;
        }
        int newScrollX = root.getChildAt(position).getLeft();
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

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (isInEditMode())
            return;
        FrameLayout tabContainer = childList.get(currentPosition);
        TextView tabContent = (TextView) tabContainer.getChildAt(0);
        float containerLeft = tabContainer.getLeft();
        int containerWidth = tabContainer.getWidth();
        float contentLeft = tabContent.getLeft();
        int contentWidth = tabContent.getWidth();
        int containerExtraWidth = (containerWidth - contentWidth) / 2;
        if (animateFromPosition) {
            long newTime = SystemClock.uptimeMillis();
            long dt = newTime - lastAnimationTime;
            lastAnimationTime = newTime;
            animationDuration += dt / 150.0f;
            if (animationDuration >= 1.0f) {
                animationDuration = 1.0f;
                animateFromPosition = false;
            }
            containerLeft = startAnimationP + (containerLeft - startAnimationP) * CubicBezierInterpolator.EASE_OUT_QUINT.getInterpolation(animationDuration);
            invalidate();
        }
        paint.setColor(indicatorColor);
        canvas.drawRoundRect(new RectF(containerLeft + contentLeft, getHeight() - indicatorHeight / 4 * 3, containerLeft + containerWidth - containerExtraWidth, (float) (getHeight() + indicatorHeight / 4 * 3)), LayoutCreator.dp(4), LayoutCreator.dp(4), paint);
    }

    public void onScroll(int position) {
        if (currentPosition == position)
            return;
        View currentTab = childList.get(currentPosition);
        if (currentTab != null) {
            startAnimationP = currentTab.getLeft();
            animationDuration = 0.0f;
            animateFromPosition = true;
            lastAnimationTime = SystemClock.uptimeMillis();
        } else
            animateFromPosition = false;

        currentPosition = position;
        if (position >= childList.size())
            return;
        animationDuration = 0.0f;
        for (int i = 0; i < childList.size(); i++) {
            root.getChildAt(i).setSelected(i == position);
        }
        scrollToChild(position);
        invalidate();
    }

    public int getChildListSize() {
        return childList.size();
    }

    public interface Listener {
        void onTabSelected(int index);
    }
}
