package net.iGap.messenger.ui.cell;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.core.content.res.ResourcesCompat;

import net.iGap.G;
import net.iGap.R;
import net.iGap.helper.LayoutCreator;
import net.iGap.messenger.theme.Theme;

public class ManageChatTextCell extends FrameLayout {

    private SimpleTextView textView;
    private SimpleTextView valueTextView;
    private ImageView imageView;
    private boolean divider;
    private String dividerColor;

    public ManageChatTextCell(Context context) {
        super(context);

        textView = new SimpleTextView(context);
        textView.setTextColor(Theme.getColor(Theme.key_default_text));
        textView.setTextSize(16);
        textView.setGravity(G.isAppRtl ? Gravity.RIGHT : Gravity.LEFT);
        textView.setTypeface(ResourcesCompat.getFont(context, R.font.main_font));
        addView(textView);

        valueTextView = new SimpleTextView(context);
        valueTextView.setTextColor(Theme.getColor(Theme.key_title_text));
        valueTextView.setTextSize(16);
        valueTextView.setGravity(G.isAppRtl ? Gravity.LEFT : Gravity.RIGHT);
        valueTextView.setTypeface(ResourcesCompat.getFont(context, R.font.main_font));
        addView(valueTextView);

        imageView = new ImageView(context);
        imageView.setScaleType(ImageView.ScaleType.CENTER);
        imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_icon), PorterDuff.Mode.MULTIPLY));
        addView(imageView);
    }

    public SimpleTextView getTextView() {
        return textView;
    }

    public SimpleTextView getValueTextView() {
        return valueTextView;
    }

    public void setDividerColor(String key) {
        dividerColor = key;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = LayoutCreator.dp(48);

        valueTextView.measure(MeasureSpec.makeMeasureSpec(width - LayoutCreator.dp(24), MeasureSpec.AT_MOST), MeasureSpec.makeMeasureSpec(LayoutCreator.dp(20), MeasureSpec.EXACTLY));
        textView.measure(MeasureSpec.makeMeasureSpec(width - LayoutCreator.dp(71 + 24), MeasureSpec.AT_MOST), MeasureSpec.makeMeasureSpec(LayoutCreator.dp(20), MeasureSpec.EXACTLY));
        imageView.measure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.AT_MOST), MeasureSpec.makeMeasureSpec(height, MeasureSpec.AT_MOST));

        setMeasuredDimension(width, LayoutCreator.dp(56) + (divider ? 1 : 0));
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int height = bottom - top;
        int width = right - left;

        int viewTop = (height - valueTextView.getTextHeight()) / 2;
        int viewLeft = G.isAppRtl ? LayoutCreator.dp(24) : 0;
        valueTextView.layout(viewLeft, viewTop, viewLeft + valueTextView.getMeasuredWidth(), viewTop + valueTextView.getMeasuredHeight());

        viewTop = (height - textView.getTextHeight()) / 2;
        viewLeft = !G.isAppRtl ? LayoutCreator.dp(71) : LayoutCreator.dp(24);
        textView.layout(viewLeft, viewTop, viewLeft + textView.getMeasuredWidth(), viewTop + textView.getMeasuredHeight());

        viewTop = LayoutCreator.dp(9);
        viewLeft = !G.isAppRtl ? LayoutCreator.dp(21) : width - imageView.getMeasuredWidth() - LayoutCreator.dp(21);
        imageView.layout(viewLeft, viewTop, viewLeft + imageView.getMeasuredWidth(), viewTop + imageView.getMeasuredHeight());
    }

    public void setTextColor(int color) {
        textView.setTextColor(color);
        valueTextView.setTextColor(color);
        imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_icon), PorterDuff.Mode.MULTIPLY));
        invalidate();
    }

    public void setColors(String icon, String text) {
        textView.setTextColor(Theme.getColor(text));
        textView.setTag(text);
        imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(icon), PorterDuff.Mode.MULTIPLY));
        imageView.setTag(icon);
    }

    public void setText(String text, String value, int resId, boolean needDivider) {
        setText(text, value, resId, 5, needDivider);
    }

    public void setText(String text, String value, int resId, int paddingTop, boolean needDivider) {
        textView.setText(text);
        if (value != null) {
            valueTextView.setText(value);
            valueTextView.setVisibility(VISIBLE);
        } else {
            valueTextView.setVisibility(INVISIBLE);
        }
        imageView.setPadding(0, LayoutCreator.dp(paddingTop), 0, 0);
        imageView.setImageResource(resId);
        divider = needDivider;
        setWillNotDraw(!divider);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (divider && Theme.dividerPaint != null) {
            canvas.drawLine(G.isAppRtl ? 0 : LayoutCreator.dp(20), getMeasuredHeight() - 1, getMeasuredWidth() - (G.isAppRtl ? LayoutCreator.dp(20) : 0), getMeasuredHeight() - 1, Theme.getDividerPaint());
        }
    }
}
