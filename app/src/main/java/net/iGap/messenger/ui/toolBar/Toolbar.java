package net.iGap.messenger.ui.toolBar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.core.content.res.ResourcesCompat;

import net.iGap.G;
import net.iGap.R;
import net.iGap.helper.LayoutCreator;
import net.iGap.module.Theme;

public class Toolbar extends FrameLayout {
    public static final int SEARCH_TAG = 1020;
    private TextView titleTextView;
    private TextView subTitleTextView;
    private ImageView backIcon;
    private ToolbarListener listener;
    private ToolbarItems items;
    private ToolbarItems actionItems;
    private String actionModeTag;
    private boolean isSearchBoxVisible;
    private int actionModeColor;
    private int extraHeight;
    private boolean actionItemsVisible;
    private AnimatorSet actionModeAnimation;
    private boolean titleIsFontIcon;

    public Toolbar(@NonNull Context context) {
        super(context);
        setBackgroundColor(Theme.getInstance().getToolbarBackgroundColor(context));
    }

    public void setTitle(String title) {
        if (titleTextView == null) {
            createTitleTextView();
        }
        titleIsFontIcon = false;
        titleTextView.setText(title);
    }

    public void setTitle(@StringRes int title) {
        if (titleTextView == null) {
            titleTextView = new TextView(getContext());
            titleTextView.setSingleLine(true);
            addView(titleTextView);
        }
        titleIsFontIcon = true;
        titleTextView.setGravity(Gravity.LEFT);
        titleTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 52);
        titleTextView.setTextColor(0xffffffff);
        titleTextView.setTypeface(ResourcesCompat.getFont(getContext(), R.font.font_icon));
        titleTextView.setText(title);
    }

    public void setSubTitle(String subTitle) {
        if (subTitleTextView == null) {
            createSubtitleTextView();
        }
        subTitleTextView.setText(subTitle);
    }

    public void setBackIcon(Drawable drawable) {
        if (backIcon == null) {
            createBackButtonImage();
        }
        backIcon.setImageDrawable(drawable);
    }

    public void setBackIcon(@StringRes int iconRes) {
        setBackIcon(getResources().getDrawable(iconRes));
    }

    public void setBackIconToNull() {
        backIcon = null;
    }

    public void setListener(ToolbarListener listener) {
        this.listener = listener;
    }

    public ToolbarItem addItem(int tag, String text, int icon, @ColorInt int color) {
        if (items == null) {
            items = new ToolbarItems(getContext(), this);
            addView(items, 0, LayoutCreator.createFrame(LayoutCreator.WRAP_CONTENT, LayoutCreator.MATCH_PARENT, Gravity.RIGHT));
        }

        return items.addItem(tag, text, icon, 54, color);
    }

    public void onSearchVisibilityChanged(boolean toggleSearch) {
        isSearchBoxVisible = toggleSearch;
        if (titleTextView != null && titleTextView.length() != 0) {
            titleTextView.setVisibility(toggleSearch ? INVISIBLE : VISIBLE);
        }

        if (subTitleTextView != null && subTitleTextView.length() != 0) {
            subTitleTextView.setVisibility(toggleSearch ? INVISIBLE : VISIBLE);
        }
    }

    public interface ToolbarListener {
        void onItemClick(int i);
    }

    public ToolbarItems createToolbarItems() {
        if (items != null) {
            return items;
        }
        items = new ToolbarItems(getContext(), this);
        items.isActionMode = true;
        addView(items, 0, LayoutCreator.createFrame(LayoutCreator.WRAP_CONTENT, LayoutCreator.MATCH_PARENT, Gravity.RIGHT));
        return items;
    }

    private void createBackButtonImage() {
        if (backIcon != null) {
            return;
        }
        backIcon = new ImageView(getContext());
        backIcon.setScaleType(ImageView.ScaleType.CENTER);
        backIcon.setOnClickListener(v -> {
            if (isSearchBoxVisible) {
                closeSearchBox();
            }
            if (listener != null) {
                listener.onItemClick(-1);
            }
        });
        addView(backIcon, LayoutCreator.createFrame(LayoutCreator.WRAP_CONTENT, LayoutCreator.WRAP_CONTENT, Gravity.TOP | Gravity.LEFT));
    }

    private void closeSearchBox() {
        closeSearchBox(true);
    }

    public void closeSearchBox(boolean closeKeyboard) {
        if (!isSearchBoxVisible || items == null) {
            return;
        }
        items.closeSearchBox(closeKeyboard);
    }

    private void createTitleTextView() {
        if (titleTextView != null) {
            return;
        }

        titleTextView = new TextView(getContext());
        titleTextView.setTextColor(Theme.getInstance().getPrimaryTextColor(getContext()));
        titleTextView.setTypeface(ResourcesCompat.getFont(getContext(), R.font.main_font_bold));
        titleTextView.setGravity(Gravity.LEFT);
        titleTextView.setSingleLine();
        titleTextView.setEllipsize(TextUtils.TruncateAt.END);
        addView(titleTextView);
    }

    private void createSubtitleTextView() {
        if (subTitleTextView != null) {
            return;
        }

        subTitleTextView = new TextView(getContext());
        subTitleTextView.setTextColor(Theme.getInstance().getTitleTextColor(getContext()));
        subTitleTextView.setTypeface(ResourcesCompat.getFont(getContext(), R.font.main_font));
        subTitleTextView.setGravity(Gravity.LEFT);
        subTitleTextView.setSingleLine();
        subTitleTextView.setEllipsize(TextUtils.TruncateAt.END);
        addView(subTitleTextView);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int actionBarHeight = getCurrentActionBarHeight();
        int actionBarHeightSpec = MeasureSpec.makeMeasureSpec(actionBarHeight, MeasureSpec.EXACTLY);
        int titleLeft;

        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), actionBarHeight);

        if (backIcon != null && backIcon.getVisibility() != GONE) {
            backIcon.measure(MeasureSpec.makeMeasureSpec(LayoutCreator.dp(54), MeasureSpec.EXACTLY), backIcon.getMeasuredHeight());
            titleLeft = LayoutCreator.dp(64); // 56 icon width + 8 margin
        } else {
            titleLeft = LayoutCreator.dp(16);
        }

        int menuWidth = 0;
        if (items != null && items.getVisibility() != GONE) {
            if (isSearchBoxVisible) {
                menuWidth = MeasureSpec.makeMeasureSpec(width - LayoutCreator.dp(64), MeasureSpec.EXACTLY);
            } else {
                menuWidth = MeasureSpec.makeMeasureSpec(width, MeasureSpec.AT_MOST);
            }
            items.measure(menuWidth, actionBarHeightSpec);
        }

        if (titleTextView != null && titleTextView.getVisibility() != GONE || subTitleTextView != null && subTitleTextView.getVisibility() != GONE) {
            int textWidth = width - (items != null ? items.getMeasuredWidth() : 0) - LayoutCreator.dp(16) - titleLeft;

            if (titleTextView != null && titleTextView.getVisibility() != GONE) {
                if (subTitleTextView != null && subTitleTextView.getVisibility() != GONE) {
                    if (!titleIsFontIcon)
                        titleTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17);
                    subTitleTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
                } else {
                    if (!titleIsFontIcon)
                        titleTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17);
                }
                titleTextView.measure(MeasureSpec.makeMeasureSpec(textWidth, MeasureSpec.AT_MOST), MeasureSpec.makeMeasureSpec(height, MeasureSpec.AT_MOST));
            }
            if (subTitleTextView != null && subTitleTextView.getVisibility() != GONE) {
                subTitleTextView.measure(MeasureSpec.makeMeasureSpec(textWidth, MeasureSpec.AT_MOST), MeasureSpec.makeMeasureSpec(height, MeasureSpec.AT_MOST));
            }
        }

        for (int i = 0, count = getChildCount(); i < count; i++) {
            final View child = getChildAt(i);

            if (child.getVisibility() == GONE || child == backIcon || child == titleTextView || child == subTitleTextView || child == items) {
                continue;
            }
            measureChildWithMargins(child, menuWidth, 0, MeasureSpec.makeMeasureSpec(getMeasuredHeight(), MeasureSpec.EXACTLY), 0);
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int titleLeft;
        int titleTop;
        if (backIcon != null && backIcon.getVisibility() != GONE) {
            backIcon.layout(0, 0, backIcon.getMeasuredWidth(), getMeasuredHeight());
            titleLeft = LayoutCreator.dp(64); // 56 icon with + 8 margin
            if (backIcon.getDrawable() == null) {
                titleLeft = LayoutCreator.dp(16);
            }
        } else {
            titleLeft = LayoutCreator.dp(16);
        }


        if (titleTextView != null && titleTextView.getVisibility() != GONE) {
            int titleTextHeight = titleTextView.getMeasuredHeight();

            if (subTitleTextView != null && subTitleTextView.getVisibility() != GONE) {
                titleTop = (getMeasuredHeight() / 2 - titleTextHeight) / 2 + LayoutCreator.dp(5);
            } else {
                titleTop = (getMeasuredHeight() - titleTextHeight) / 2;
            }

            titleTextView.layout(titleLeft, titleTop, titleLeft + titleTextView.getMeasuredWidth(), titleTop + titleTextHeight);
        }

        if (subTitleTextView != null && subTitleTextView.getVisibility() != GONE) {
            int textTop = getMeasuredHeight() / 2 + (getMeasuredHeight() / 2 - subTitleTextView.getMeasuredHeight()) / 2 - LayoutCreator.dp(4);
            subTitleTextView.layout(titleLeft, textTop, titleLeft + subTitleTextView.getMeasuredWidth(), textTop + subTitleTextView.getMeasuredHeight());
        }

        if (items != null) {
            int menuLeft = isSearchBoxVisible ? LayoutCreator.dp(64) : getMeasuredWidth() - items.getMeasuredWidth();
            items.layout(menuLeft, 0, menuLeft + items.getMeasuredWidth(), items.getMeasuredHeight());
        }

        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == GONE || child == titleTextView || child == subTitleTextView || child == items || child == backIcon) {
                continue;
            }

            LayoutParams lp = (LayoutParams) child.getLayoutParams();

            int width = child.getMeasuredWidth();
            int height = child.getMeasuredHeight();
            int childLeft;
            int childTop;

            int gravity = lp.gravity;
            if (gravity == -1) {
                gravity = Gravity.TOP | Gravity.LEFT;
            }

            final int absoluteGravity = gravity & Gravity.HORIZONTAL_GRAVITY_MASK;
            final int verticalGravity = gravity & Gravity.VERTICAL_GRAVITY_MASK;

            switch (absoluteGravity & Gravity.HORIZONTAL_GRAVITY_MASK) {
                case Gravity.CENTER_HORIZONTAL:
                    childLeft = (right - left - width) / 2 + lp.leftMargin - lp.rightMargin;
                    break;
                case Gravity.RIGHT:
                    childLeft = right - width - lp.rightMargin;
                    break;
                case Gravity.LEFT:
                default:
                    childLeft = lp.leftMargin;
            }

            switch (verticalGravity) {
                case Gravity.CENTER_VERTICAL:
                    childTop = (bottom - top - height) / 2 + lp.topMargin - lp.bottomMargin;
                    break;
                case Gravity.BOTTOM:
                    childTop = (bottom - top) - height - lp.bottomMargin;
                    break;
                default:
                    childTop = lp.topMargin;
            }
            child.layout(childLeft, childTop, childLeft + width, childTop + height);
        }
    }

    public boolean isInActionMode(String tag) {
        return actionItems != null && (actionModeTag == null && tag == null) || (actionModeTag != null && actionModeTag.equals(tag));
    }

    public ImageView getBackIcon() {
        return backIcon;
    }

    public ToolbarItems createActionToolbar(String tag) {
        if (isInActionMode(tag)) {
            return actionItems;
        }
        if (actionItems != null) {
            removeView(actionItems);
            actionItems = null;
        }

        actionModeTag = tag;
        actionItems = new ToolbarItems(getContext(), this) {
            @Override
            public void setBackgroundColor(int color) {
                super.setBackgroundColor(actionModeColor = color);
            }
        };

        actionItems.isActionMode = true;
        actionItems.setClickable(true);
        actionItems.setBackgroundColor(Theme.getInstance().getToolbarBackgroundColor(getContext()));
        addView(actionItems, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.MATCH_PARENT, Gravity.RIGHT));
        actionItems.setVisibility(INVISIBLE);

        return actionItems;
    }

    public static int getCurrentActionBarHeight() {
        if (G.twoPaneMode) {
            return LayoutCreator.dp(68);
        } else {
            return LayoutCreator.dp(60);
        }
    }

    public void showActionToolbar() {
        if (actionItems == null || actionItemsVisible) {
            return;
        }

        actionItemsVisible = true;

        if (actionModeAnimation != null) {
            actionModeAnimation.cancel();
        }
        actionModeAnimation = new AnimatorSet();
        actionModeAnimation.playTogether(
                ObjectAnimator.ofFloat(actionItems, View.ALPHA, 0.0f, 1.0f),
                ObjectAnimator.ofFloat(items, View.ALPHA, 1.0f, 0.0f),
                ObjectAnimator.ofObject(this, "backgroundColor", new ArgbEvaluator(), Theme.getInstance().getToolbarBackgroundColor(getContext()), Theme.getInstance().getToolbarActionModeBackgroundColor(getContext()))
        );
        actionModeAnimation.setDuration(100);
        actionModeAnimation.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                actionItems.setVisibility(VISIBLE);

                if (titleTextView != null) {
                    titleTextView.setVisibility(INVISIBLE);
                }

                if (subTitleTextView != null && !TextUtils.isEmpty(subTitleTextView.getText())) {
                    subTitleTextView.setVisibility(INVISIBLE);
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (actionModeAnimation != null && actionModeAnimation.equals(animation)) {
                    actionModeAnimation = null;
                    if (titleTextView != null) {
                        titleTextView.setVisibility(INVISIBLE);
                    }

                    if (subTitleTextView != null && !TextUtils.isEmpty(subTitleTextView.getText())) {
                        subTitleTextView.setVisibility(INVISIBLE);
                    }
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                if (actionModeAnimation != null && actionModeAnimation.equals(animation)) {
                    actionModeAnimation = null;
                }
            }
        });
        actionModeAnimation.start();

        if (backIcon != null) {
            Drawable drawable = backIcon.getDrawable();
            if (drawable instanceof BackDrawable) {
                ((BackDrawable) drawable).setRotation(1, true);
            }
        }
    }

    public void hideActionToolbar() {
        if (actionItems == null || !actionItemsVisible) {
            return;
        }

        actionItemsVisible = false;
        if (actionModeAnimation != null) {
            actionModeAnimation.cancel();
        }

        actionModeAnimation = new AnimatorSet();
        actionModeAnimation.playTogether(
                ObjectAnimator.ofFloat(actionItems, View.ALPHA, 0.0f),
                ObjectAnimator.ofFloat(items, View.ALPHA, 1.0f),
                ObjectAnimator.ofObject(this, "backgroundColor", new ArgbEvaluator(), Theme.getInstance().getToolbarActionModeBackgroundColor(getContext()), Theme.getInstance().getToolbarBackgroundColor(getContext()))
        );
        actionModeAnimation.setDuration(100);
        actionModeAnimation.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (actionModeAnimation != null && actionModeAnimation.equals(animation)) {
                    actionModeAnimation = null;
                    actionItems.setVisibility(INVISIBLE);
                }

                if (!isSearchBoxVisible) {
                    if (titleTextView != null) {
                        titleTextView.setVisibility(VISIBLE);
                    }
                    if (subTitleTextView != null && !TextUtils.isEmpty(subTitleTextView.getText())) {
                        subTitleTextView.setVisibility(VISIBLE);
                    }
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                if (actionModeAnimation != null && actionModeAnimation.equals(animation)) {
                    actionModeAnimation = null;
                }
            }
        });
        actionModeAnimation.start();

        if (backIcon != null) {
            Drawable drawable = backIcon.getDrawable();
            if (drawable instanceof BackDrawable) {
                ((BackDrawable) drawable).setRotation(1, true);
            }
        }
    }

    public void setActionModeColor(int color) {
        if (actionItems != null) {
            actionItems.setBackgroundColor(color);
        }
    }

    public ToolbarItems getActionMode() {
        return actionItems;
    }

}
