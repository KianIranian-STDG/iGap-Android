package net.iGap.messenger.ui.cell;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import net.iGap.G;
import net.iGap.R;
import net.iGap.helper.LayoutCreator;
import net.iGap.messenger.theme.Theme;
import net.iGap.proto.ProtoUserIVandGetScore;

public class TextCell extends FrameLayout {

    private final SimpleTextView textView;
    private final SimpleTextView valueTextView;
    private final ImageView imageView;
    private final ImageView valueImageView;
    private final int leftPadding;
    private boolean needDivider;
    private int offsetFromImage = 71;
    private int imageLeft = 21;
    private boolean inDialogs;

    public TextCell(Context context) {
        this(context, 23, true);
    }

    public TextCell(Context context, int left, boolean dialog) {
        super(context);

        leftPadding = left;

        textView = new SimpleTextView(context);
        textView.setTextColor(Theme.getColor(Theme.key_default_text));
        textView.setTextSize(16);
        textView.setGravity(G.isAppRtl ? Gravity.RIGHT : Gravity.LEFT);
        textView.setImportantForAccessibility(IMPORTANT_FOR_ACCESSIBILITY_NO);
        textView.setTypeface(ResourcesCompat.getFont(context, R.font.main_font));
        addView(textView);

        valueTextView = new SimpleTextView(context);
        valueTextView.setTextColor(Theme.getColor(Theme.key_title_text));
        valueTextView.setTextSize(16);
        valueTextView.setGravity(G.isAppRtl ? Gravity.LEFT : Gravity.RIGHT);
        valueTextView.setImportantForAccessibility(IMPORTANT_FOR_ACCESSIBILITY_NO);
        valueTextView.setTypeface(ResourcesCompat.getFont(context, R.font.main_font));
        addView(valueTextView);

        imageView = new ImageView(context);
        imageView.setScaleType(ImageView.ScaleType.CENTER);
        imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_icon), PorterDuff.Mode.MULTIPLY));
        addView(imageView);

        valueImageView = new ImageView(context);
        valueImageView.setScaleType(ImageView.ScaleType.CENTER);
        addView(valueImageView);

        setFocusable(true);
    }

    public void setIsInDialogs() {
        inDialogs = true;
    }

    public SimpleTextView getTextView() {
        return textView;
    }

    public ImageView getImageView() {
        return imageView;
    }

    public SimpleTextView getValueTextView() {
        return valueTextView;
    }

    public ImageView getValueImageView() {
        return valueImageView;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = LayoutCreator.dp(48);

        valueTextView.measure(MeasureSpec.makeMeasureSpec(width - LayoutCreator.dp(leftPadding), MeasureSpec.AT_MOST), MeasureSpec.makeMeasureSpec(LayoutCreator.dp(20), MeasureSpec.EXACTLY));
        textView.measure(MeasureSpec.makeMeasureSpec(width - LayoutCreator.dp(71 + leftPadding) - valueTextView.getTextWidth(), MeasureSpec.AT_MOST), MeasureSpec.makeMeasureSpec(LayoutCreator.dp(20), MeasureSpec.EXACTLY));
        if (imageView.getVisibility() == VISIBLE) {
            imageView.measure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.AT_MOST), MeasureSpec.makeMeasureSpec(height, MeasureSpec.AT_MOST));
        }
        if (valueImageView.getVisibility() == VISIBLE) {
            valueImageView.measure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.AT_MOST), MeasureSpec.makeMeasureSpec(height, MeasureSpec.AT_MOST));
        }
        setMeasuredDimension(width, LayoutCreator.dp(50) + (needDivider ? 1 : 0));
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int height = bottom - top;
        int width = right - left;

        int viewTop = (height - valueTextView.getTextHeight()) / 2;
        int viewLeft = G.isAppRtl ? LayoutCreator.dp(leftPadding) : 0;
        valueTextView.layout(viewLeft, viewTop, viewLeft + valueTextView.getMeasuredWidth(), viewTop + valueTextView.getMeasuredHeight());

        viewTop = (height - textView.getTextHeight()) / 2;
        if (G.isAppRtl) {
            viewLeft = getMeasuredWidth() - textView.getMeasuredWidth() - LayoutCreator.dp(imageView.getVisibility() == VISIBLE ? offsetFromImage : leftPadding);
        } else {
            viewLeft = LayoutCreator.dp(imageView.getVisibility() == VISIBLE ? offsetFromImage : leftPadding);
        }
        textView.layout(viewLeft, viewTop, viewLeft + textView.getMeasuredWidth(), viewTop + textView.getMeasuredHeight());

        if (imageView.getVisibility() == VISIBLE) {
            viewTop = LayoutCreator.dp(5);
            viewLeft = !G.isAppRtl ? LayoutCreator.dp(imageLeft) : width - imageView.getMeasuredWidth() - LayoutCreator.dp(imageLeft);
            imageView.layout(viewLeft, viewTop, viewLeft + imageView.getMeasuredWidth(), viewTop + imageView.getMeasuredHeight());
        }

        if (valueImageView.getVisibility() == VISIBLE) {
            viewTop = (height - valueImageView.getMeasuredHeight()) / 2;
            viewLeft = G.isAppRtl ? LayoutCreator.dp(23) : width - valueImageView.getMeasuredWidth() - LayoutCreator.dp(23);
            valueImageView.layout(viewLeft, viewTop, viewLeft + valueImageView.getMeasuredWidth(), viewTop + valueImageView.getMeasuredHeight());
        }
    }

    public void setTextColor(int color) {
        textView.setTextColor(color);
        valueTextView.setTextColor(Theme.getColor(Theme.key_title_text));
        invalidate();
    }

    public void setColors(String icon, String text) {
        textView.setTextColor(Theme.getColor(text));
        textView.setTag(text);
        if (icon != null) {
            imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(icon), PorterDuff.Mode.MULTIPLY));
            imageView.setTag(icon);
        }
    }

    public void setText(String text, boolean divider) {
        textView.setText(text);
        valueTextView.setText(null);
        imageView.setVisibility(GONE);
        valueTextView.setVisibility(GONE);
        valueImageView.setVisibility(GONE);
        needDivider = divider;
        setWillNotDraw(!needDivider);
    }

    public void setTextAndIcon(String text, int resId, boolean divider) {
        textView.setText(text);
        valueTextView.setText(null);
        imageView.setImageResource(resId);
        imageView.setVisibility(VISIBLE);
        valueTextView.setVisibility(GONE);
        valueImageView.setVisibility(GONE);
        imageView.setPadding(0, LayoutCreator.dp(7), 0, 0);
        needDivider = divider;
        setWillNotDraw(!needDivider);
    }

    public void setTextAndIcon(String text, Drawable drawable, boolean divider) {
        offsetFromImage = 68;
        imageLeft = 18;
        textView.setText(text);
        valueTextView.setText(null);
        imageView.setColorFilter(null);
        imageView.setImageDrawable(drawable);
        imageView.setVisibility(VISIBLE);
        valueTextView.setVisibility(GONE);
        valueImageView.setVisibility(GONE);
        imageView.setPadding(0, LayoutCreator.dp(6), 0, 0);
        needDivider = divider;
        setWillNotDraw(!needDivider);
    }

    public void setOffsetFromImage(int value) {
        offsetFromImage = value;
    }

    public void setTextAndValue(String text, String value, boolean divider) {
        textView.setText(text);
        valueTextView.setText(value);
        valueTextView.setVisibility(VISIBLE);
        imageView.setVisibility(GONE);
        valueImageView.setVisibility(GONE);
        needDivider = divider;
        setWillNotDraw(!needDivider);
    }

    public void setTextAndValueAndIcon(String text, String value, int resId, boolean divider) {
        textView.setText(text);
        valueTextView.setText(value);
        valueTextView.setVisibility(VISIBLE);
        valueImageView.setVisibility(GONE);
        imageView.setVisibility(VISIBLE);
        imageView.setPadding(0, LayoutCreator.dp(7), 0, 0);
        imageView.setImageResource(resId);
        needDivider = divider;
        setWillNotDraw(!needDivider);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void setTextAndValueAndIcon(String text, int resId, int resValueId , boolean divider) {
        textView.setText(text);
        valueTextView.setVisibility(GONE);
        valueImageView.setVisibility(VISIBLE);
        valueImageView.setPadding(0, LayoutCreator.dp(7), 0, 0);
        valueImageView.setImageDrawable(Theme.tintDrawable(getContext().getDrawable(resValueId),getContext(),Theme.getColor(Theme.key_icon)));
        imageView.setVisibility(VISIBLE);
        imageView.setPadding(0, LayoutCreator.dp(7), 0, 0);
        imageView.setImageResource(resId);

        needDivider = divider;
        setWillNotDraw(!needDivider);
    }

    public void setTextAndValueDrawable(String text, Drawable drawable, boolean divider) {
        textView.setText(text);
        valueTextView.setText(null);
        valueImageView.setVisibility(VISIBLE);
        valueImageView.setImageDrawable(drawable);
        valueTextView.setVisibility(GONE);
        imageView.setVisibility(GONE);
        imageView.setPadding(0, LayoutCreator.dp(7), 0, 0);
        needDivider = divider;
        setWillNotDraw(!needDivider);
    }

    @SuppressLint("DefaultLocale")
    public void setScoreValue(ProtoUserIVandGetScore.UserIVandGetScoreResponse.IVandScore score, boolean divider) {
        imageView.setVisibility(GONE);
        textView.setText(score.getFaName());
        if (score.getScore() > 0) {
            valueTextView.setTextColor(Theme.getColor(Theme.key_theme_color));
            valueTextView.setText(String.format(G.isAppRtl ? "+%d" : "%d+", Math.abs(score.getScore())));
        } else {
            valueTextView.setTextColor(Theme.getColor(Theme.key_theme_color));
            valueTextView.setText(String.format(G.isAppRtl ? "-%d" : "%d-" , Math.abs(score.getScore())));
        }
        needDivider = divider;
        setWillNotDraw(!needDivider);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (needDivider && Theme.dividerPaint != null) {
            canvas.drawLine(G.isAppRtl ? 0 : LayoutCreator.dp(imageView.getVisibility() == VISIBLE ? (inDialogs ? 72 : 68) : 20), getMeasuredHeight() - 1, getMeasuredWidth() - (G.isAppRtl ? LayoutCreator.dp(imageView.getVisibility() == VISIBLE ? (inDialogs ? 72 : 68) : 20) : 0), getMeasuredHeight() - 1, Theme.dividerPaint);
        }
    }

    @Override
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        final CharSequence text = textView.getText();
        if (!TextUtils.isEmpty(text)) {
            final CharSequence valueText = valueTextView.getText();
            if (!TextUtils.isEmpty(valueText)) {
                info.setText(text + ": " + valueText);
            } else {
                info.setText(text);
            }
        }
    }
}
