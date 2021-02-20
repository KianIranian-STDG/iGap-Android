package net.iGap.messenger.ui.toolBar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Build;
import android.view.View;
import android.widget.PopupWindow;

import net.iGap.helper.LayoutCreator;

public class ToolbarPopup extends PopupWindow {

    private AnimatorSet popupAnimatorSet;
    private boolean animationEnabled = Build.VERSION.SDK_INT >= 18;
    private boolean isClosingAnimated;
    private long dismissAnimationDuration = 150;

    public ToolbarPopup() {
    }

    public ToolbarPopup(View contentView) {
        super(contentView);
    }

    public ToolbarPopup(int width, int height) {
        super(width, height);
    }

    public ToolbarPopup(View contentView, int width, int height) {
        super(contentView, width, height);
    }

    public ToolbarPopup(View contentView, int width, int height, boolean focusable) {
        super(contentView, width, height, focusable);
    }


    public void startAnimation() {
        if (animationEnabled) {
            if (popupAnimatorSet != null) {
                return;
            }
            ToolbarMenuItemLayout content = (ToolbarMenuItemLayout) getContentView();
            content.setTranslationY(0);
            content.setAlpha(1.0f);
            content.setPivotX(content.getMeasuredWidth());
            content.setPivotY(0);
            int count = content.getChildCount();
            int visibleCount = 0;

            for (int a = 0; a < count; a++) {
                View child = content.getChildAt(a);
                child.setAlpha(0.0f);
                if (child.getVisibility() != View.VISIBLE) {
                    continue;
                }
                content.positions.put(child, visibleCount);
                visibleCount++;
            }

            popupAnimatorSet = new AnimatorSet();
            popupAnimatorSet.playTogether(
                    ObjectAnimator.ofFloat(content, "backScaleY", 0.0f, 1.0f),
                    ObjectAnimator.ofFloat(content, "backAlpha", 0, 255)
            );

            popupAnimatorSet.setDuration(150 + 16 * visibleCount);
            popupAnimatorSet.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    popupAnimatorSet = null;
                    ToolbarMenuItemLayout content = (ToolbarMenuItemLayout) getContentView();
                    int count = content.getChildCount();
                    for (int a = 0; a < count; a++) {
                        View child = content.getChildAt(a);
                        child.setAlpha(1.0f);
                    }
                }
            });
            popupAnimatorSet.start();
        }
    }

    @Override
    public void dismiss() {
        dismiss(true);
    }

    public void dismiss(boolean animated) {
        setFocusable(false);
        if (popupAnimatorSet != null) {
            if (animated && isClosingAnimated) {
                return;
            }
            popupAnimatorSet.cancel();
            popupAnimatorSet = null;
        }
        isClosingAnimated = false;
        if (animationEnabled && animated) {
            isClosingAnimated = true;
            ToolbarMenuItemLayout content = (ToolbarMenuItemLayout) getContentView();
            if (content.itemAnimators != null && !content.itemAnimators.isEmpty()) {
                for (int a = 0, N = content.itemAnimators.size(); a < N; a++) {
                    AnimatorSet animatorSet = content.itemAnimators.get(a);
                    animatorSet.removeAllListeners();
                    animatorSet.cancel();
                }
                content.itemAnimators.clear();
            }
            popupAnimatorSet = new AnimatorSet();
            popupAnimatorSet.playTogether(
                    ObjectAnimator.ofFloat(content, View.TRANSLATION_Y, LayoutCreator.dp(content.showedFromBottom ? 5 : -5)),
                    ObjectAnimator.ofFloat(content, View.ALPHA, 0.0f));
            popupAnimatorSet.setDuration(dismissAnimationDuration);
            popupAnimatorSet.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    popupAnimatorSet = null;
                    isClosingAnimated = false;
                    setFocusable(false);
                    try {
                        ToolbarPopup.super.dismiss();
                    } catch (Exception ignore) {

                    }
                   /* unregisterListener();
                    if (pauseNotifications) {
                        NotificationCenter.getInstance(currentAccount).onAnimationFinish(popupAnimationIndex);
                    }*/
                }
            });
            /*if (pauseNotifications) {
                popupAnimationIndex = NotificationCenter.getInstance(currentAccount).setAnimationInProgress(popupAnimationIndex, null);
            }*/
            popupAnimatorSet.start();
        } else {
            try {
                super.dismiss();
            } catch (Exception ignore) {

            }
//            unregisterListener();
        }
    }

}
