package net.iGap.messenger.ui.components;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import net.iGap.R;
import net.iGap.helper.LayoutCreator;
import net.iGap.messenger.dialog.RadialProgressView;
import net.iGap.messenger.theme.Theme;

public class EmptyTextProgressView extends FrameLayout {

    private TextView textView;
    private View progressView;
    private boolean inLayout;
    private int showAtPos;

    public EmptyTextProgressView(Context context) {
        this(context, null);
    }

    public EmptyTextProgressView(Context context, View progressView) {
        super(context);

        if (progressView == null) {
            progressView = new RadialProgressView(context);
            addView(progressView, LayoutCreator.createFrame(LayoutCreator.WRAP_CONTENT, LayoutCreator.WRAP_CONTENT));
        } else {
            addView(progressView, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.MATCH_PARENT));
        }
        this.progressView = progressView;

        textView = new TextView(context);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
        textView.setTextColor(Theme.getColor(Theme.key_icon));
        textView.setGravity(Gravity.CENTER);
        textView.setPadding(LayoutCreator.dp(20), 0, LayoutCreator.dp(20), 0);
        textView.setText(context.getString(R.string.NoResult));
        addView(textView, LayoutCreator.createFrame(LayoutCreator.WRAP_CONTENT, LayoutCreator.WRAP_CONTENT));

        progressView.setAlpha(0f);
        textView.setAlpha(0f);

        setOnTouchListener((v, event) -> true);
    }

    public void showProgress() {
        textView.animate().alpha(0f).setDuration(150).start();
        progressView.animate().alpha(1f).setDuration(150).start();
    }

    public void showTextView() {
        textView.animate().alpha(1f).setDuration(150).start();
        progressView.animate().alpha(0f).setDuration(150).start();
    }

    public void setText(String text) {
        textView.setText(text);
    }

    public void setTextColor(int color) {
        textView.setTextColor(color);
        invalidate();
    }

    public void setProgressBarColor(int color) {
        if (progressView instanceof RadialProgressView) {
            ((RadialProgressView) progressView).setProgressColor(color);
        }
    }

    public void setTopImage(int resId) {
        if (resId == 0) {
            textView.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        } else {
            Drawable drawable = getContext().getResources().getDrawable(resId).mutate();
            if (drawable != null) {
                drawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_icon), PorterDuff.Mode.MULTIPLY));
            }
            textView.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null);
            textView.setCompoundDrawablePadding(LayoutCreator.dp(1));
        }
    }

    public void setTextSize(int size) {
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size);
    }

    public void setShowAtCenter(boolean value) {
        showAtPos = value ? 1 : 0;
    }

    public void setShowAtTop(boolean value) {
        showAtPos = value ? 2 : 0;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        inLayout = true;
        int width = r - l;
        int height = b - t;
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);

            if (child.getVisibility() == GONE) {
                continue;
            }

            int x = (width - child.getMeasuredWidth()) / 2;
            int y;
            if (showAtPos == 2) {
                y = (LayoutCreator.dp(100) - child.getMeasuredHeight()) / 2 + getPaddingTop();
            } else if (showAtPos == 1) {
                y = (height / 2 - child.getMeasuredHeight()) / 2 + getPaddingTop();
            } else {
                y = (height - child.getMeasuredHeight()) / 2 + getPaddingTop();
            }
            child.layout(x, y, x + child.getMeasuredWidth(), y + child.getMeasuredHeight());
        }
        inLayout = false;
    }

    @Override
    public void requestLayout() {
        if (!inLayout) {
            super.requestLayout();
        }
    }

    @Override
    public boolean hasOverlappingRendering() {
        return false;
    }
}
