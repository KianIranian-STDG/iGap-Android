package net.iGap.messenger.ui.toolBar;

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
    private boolean isSearchBoxVisible;

    public Toolbar(@NonNull Context context) {
        super(context);
        setBackgroundColor(Theme.getInstance().getButtonSelectorBackground(context));
    }

    public void setTitle(String title) {
        if (titleTextView == null) {
            createTitleTextView();
        }

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
        titleTextView.setTextColor(Theme.getInstance().getTitleTextColor(getContext()));
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
        int actionBarHeight = LayoutCreator.dp(56);
        int actionBarHeightSpec = MeasureSpec.makeMeasureSpec(actionBarHeight, MeasureSpec.EXACTLY);
        int titleLeft;

        if (backIcon != null && backIcon.getVisibility() != GONE) {
            backIcon.measure(MeasureSpec.makeMeasureSpec(LayoutCreator.dp(54), MeasureSpec.EXACTLY), backIcon.getMeasuredHeight());
            titleLeft = LayoutCreator.dp(64); // 56 icon width + 8 margin
        } else {
            titleLeft = LayoutCreator.dp(16);
        }

        if (items != null && items.getVisibility() != GONE) {
            int menuWidth;
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
                    titleTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17);
                    subTitleTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
                } else {
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
            if (child.getVisibility() != GONE) {
                if (child != backIcon && child != titleTextView && child != subTitleTextView && child != items) {
                    measureChild(child, widthMeasureSpec, heightMeasureSpec);
                }
            }
        }

        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), actionBarHeight);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int p) {
        int titleLeft;
        int titleTop;

        if (backIcon != null && backIcon.getVisibility() != GONE) {
            backIcon.layout(0, 0, backIcon.getMeasuredWidth(), getMeasuredHeight());
            titleLeft = LayoutCreator.dp(64); // 56 icon with + 8 margin
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
    }
}
