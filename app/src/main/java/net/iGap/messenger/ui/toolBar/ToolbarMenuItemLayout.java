package net.iGap.messenger.ui.toolBar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import androidx.annotation.NonNull;

import net.iGap.R;
import net.iGap.helper.LayoutCreator;
import net.iGap.messenger.theme.Theme;

import java.util.ArrayList;
import java.util.HashMap;

public class ToolbarMenuItemLayout extends FrameLayout {
    private float backAlpha = 255;
    private float backScaleX = 1;
    private float backScaleY = 1;
    private ScrollView scrollView;
    private LinearLayout linearLayout;
    private Drawable backgroundDrawable;
    private OnDispatchKeyEventListener onDispatchKeyEventListener;
    public ArrayList<AnimatorSet> itemAnimators;
    private static DecelerateInterpolator decelerateInterpolator = new DecelerateInterpolator();
    public HashMap<View, Integer> positions = new HashMap<>();
    private int backgroundColor = Color.WHITE;
    public boolean showedFromBottom;
    private int lastStartedChild;
    private boolean animationEnabled = Build.VERSION.SDK_INT >= 18;


    public interface OnDispatchKeyEventListener {
        void onDispatchKeyEvent(KeyEvent keyEvent);
    }

    public ToolbarMenuItemLayout(@NonNull Context context) {
        super(context);

        backgroundDrawable = getResources().getDrawable(R.drawable.popup_fixed_alert2).mutate();
        setBackgroundColor(Theme.getColor(Theme.key_popup_background));

        scrollView = new ScrollView(context);
        scrollView.setVerticalScrollBarEnabled(false);
        addView(scrollView, LayoutCreator.WRAP_CONTENT, LayoutCreator.WRAP_CONTENT);

        linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        if (scrollView != null) {
            scrollView.addView(linearLayout, new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        } else {
            addView(linearLayout, LayoutCreator.createFrame(LayoutCreator.WRAP_CONTENT, LayoutCreator.WRAP_CONTENT));
        }

        setPadding(LayoutCreator.dp(8), LayoutCreator.dp(8), LayoutCreator.dp(8), LayoutCreator.dp(8));
        setWillNotDraw(false);
    }

    public void setBackAlpha(float backAlpha) {
        this.backAlpha = backAlpha;
    }

    public void setBackScaleX(float backScaleX) {
        this.backScaleX = backScaleX;
    }


    public void setBackScaleY(float value) {
        backScaleY = value;
        if (animationEnabled) {
            int height = getMeasuredHeight() - LayoutCreator.dp(16);
            if (showedFromBottom) {
                for (int a = lastStartedChild; a >= 0; a--) {
                    View child = getItemAt(a);
                    if (child.getVisibility() != VISIBLE) {
                        continue;
                    }
                    Integer position = positions.get(child);
                    if (position != null && height - (position * LayoutCreator.dp(48) + LayoutCreator.dp(32)) > value * height) {
                        break;
                    }
                    lastStartedChild = a - 1;
                    startChildAnimation(child);
                }
            } else {
                int count = getItemsCount();
                for (int a = lastStartedChild; a < count; a++) {
                    View child = getItemAt(a);
                    if (child.getVisibility() != VISIBLE) {
                        continue;
                    }
                    Integer position = positions.get(child);
                    if (position != null && (position + 1) * LayoutCreator.dp(48) - LayoutCreator.dp(24) > value * height) {
                        break;
                    }
                    lastStartedChild = a + 1;
                    startChildAnimation(child);
                }
            }
        }
        invalidate();
    }

    public int getItemsCount() {
        return linearLayout.getChildCount();
    }

    private void startChildAnimation(View child) {
        if (animationEnabled) {
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(
                    ObjectAnimator.ofFloat(child, View.ALPHA, 0.0f, 1.0f),
                    ObjectAnimator.ofFloat(child, View.TRANSLATION_Y, LayoutCreator.dp(showedFromBottom ? 6 : -6), 0));
            animatorSet.setDuration(180);
            animatorSet.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    itemAnimators.remove(animatorSet);
                }
            });
            animatorSet.setInterpolator(decelerateInterpolator);
            animatorSet.start();
            if (itemAnimators == null) {
                itemAnimators = new ArrayList<>();
            }
            itemAnimators.add(animatorSet);
        }
    }

    public View getItemAt(int index) {
        return linearLayout.getChildAt(index);
    }

    public void setOnDispatchKeyEventListener(OnDispatchKeyEventListener onDispatchKeyEventListener) {
        this.onDispatchKeyEventListener = onDispatchKeyEventListener;
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    @Override
    public void setBackgroundColor(int color) {
        if (backgroundColor != color) {
            backgroundDrawable.setColorFilter(new PorterDuffColorFilter(backgroundColor = color, PorterDuff.Mode.MULTIPLY));
        }
    }

    @Override
    public void setBackgroundDrawable(Drawable drawable) {
        backgroundColor = Theme.getColor(Theme.key_popup_background);
        backgroundDrawable = drawable;
    }

    @Override
    public void addView(View child) {
        linearLayout.addView(child);
    }

    public void addView(View child, ViewGroup.LayoutParams layoutParams) {
        linearLayout.addView(child, layoutParams);
    }

    public void removeInnerViews() {
        linearLayout.removeAllViews();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (backgroundDrawable != null) {
            backgroundDrawable.setAlpha(255);

            backgroundDrawable.setBounds(0, 0, (int) (getMeasuredWidth() * backScaleX), (int) (getMeasuredHeight() * backScaleY));
            backgroundDrawable.draw(canvas);
        }
    }

    public void scrollToTop() {
        scrollView.scrollTo(0, 0);
    }

    public void setupRadialSelector(int color) {
        int count = linearLayout.getChildCount();
        for (int i = 0; i < count; i++) {
            View child = linearLayout.getChildAt(i);
            child.setBackground(Theme.createSimpleSelectorRoundRectDrawable(color, i == 0 ? 6 : 0, i == count - 1 ? 6 : 0));
        }
    }
}
