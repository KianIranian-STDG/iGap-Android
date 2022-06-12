package net.iGap.messenger.ui.cell;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;

import net.iGap.G;
import net.iGap.R;
import net.iGap.helper.LayoutCreator;
import net.iGap.messenger.Emoji;
import net.iGap.messenger.theme.Theme;

public class TextDetailSettingsCell extends FrameLayout {

    private TextView textView;
    private TextView valueTextView;
    private ImageView imageView;
    private boolean needDivider;
    private boolean multiline;

    public TextDetailSettingsCell(Context context) {
        super(context);

        textView = new TextView(context);
        textView.setTextColor(Theme.getColor(Theme.key_default_text));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        textView.setLines(1);
        textView.setMaxLines(1);
        textView.setSingleLine(true);
        textView.setEllipsize(TextUtils.TruncateAt.END);
        textView.setGravity((G.isAppRtl ? Gravity.RIGHT : Gravity.LEFT) | Gravity.CENTER_VERTICAL);
        textView.setTypeface(ResourcesCompat.getFont(context, R.font.main_font));
        addView(textView, LayoutCreator.createFrame(LayoutCreator .WRAP_CONTENT, LayoutCreator .WRAP_CONTENT, (G.isAppRtl  ? Gravity.RIGHT : Gravity.LEFT) | Gravity.TOP, 21, 10, 21, 0));

        valueTextView = new TextView(context);
        valueTextView.setTextColor(Theme.getColor(Theme.key_title_text));
        valueTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
        valueTextView.setGravity(G.isAppRtl  ? Gravity.RIGHT : Gravity.LEFT);
        valueTextView.setLines(1);
        valueTextView.setMaxLines(1);
        valueTextView.setSingleLine(true);
        valueTextView.setPadding(0, 0, 0, 0);
        valueTextView.setTypeface(ResourcesCompat.getFont(context, R.font.main_font));
        addView(valueTextView, LayoutCreator .createFrame(LayoutCreator .WRAP_CONTENT, LayoutCreator .WRAP_CONTENT, (G.isAppRtl  ? Gravity.RIGHT : Gravity.LEFT) | Gravity.TOP, 21, 35, 21, 0));

        imageView = new ImageView(context);
        imageView.setScaleType(ImageView.ScaleType.CENTER);
        imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_icon), PorterDuff.Mode.MULTIPLY));
        imageView.setVisibility(GONE);
        addView(imageView, LayoutCreator .createFrame(52, 52, (G.isAppRtl  ? Gravity.RIGHT : Gravity.LEFT) | Gravity.TOP, 8, 6, 8, 0));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (!multiline) {
            super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(LayoutCreator .dp(64) + (needDivider ? 1 : 0), MeasureSpec.EXACTLY));
        } else {
            super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
        }
    }

    public TextView getTextView() {
        return textView;
    }

    public TextView getValueTextView() {
        return valueTextView;
    }

    public void setMultilineDetail(boolean value) {
        multiline = value;
        if (value) {
            valueTextView.setLines(0);
            valueTextView.setMaxLines(0);
            valueTextView.setSingleLine(false);
            valueTextView.setPadding(0, 0, 0, LayoutCreator .dp(12));
        } else {
            valueTextView.setLines(1);
            valueTextView.setMaxLines(1);
            valueTextView.setSingleLine(true);
            valueTextView.setPadding(0, 0, 0, 0);
        }
    }

    public void setTextAndValue(String text, CharSequence value, boolean divider) {
        textView.setText(text);
        valueTextView.setText(value);
        needDivider = divider;
        imageView.setVisibility(GONE);
        setWillNotDraw(!divider);
    }

    public void setTextAndValueAndIcon(String text, CharSequence value, int resId, boolean divider) {
        textView.setText(text);
        valueTextView.setText(value);
        imageView.setImageResource(resId);
        imageView.setVisibility(VISIBLE);
        textView.setPadding(G.isAppRtl  ? 0 : LayoutCreator .dp(50), 0, G.isAppRtl  ? LayoutCreator .dp(50) : 0, 0);
        valueTextView.setPadding(G.isAppRtl  ? 0 : LayoutCreator .dp(50), 0, G.isAppRtl  ? LayoutCreator .dp(50) : 0, multiline ? LayoutCreator .dp(12) : 0);
        needDivider = divider;
        setWillNotDraw(!divider);
    }

    public void setValue(CharSequence value) {
        valueTextView.setText(value);
    }

    public void setTextWithEmojiAnd21Value(String text, CharSequence value, boolean divider) {
        textView.setText(Emoji.replaceEmoji(text, textView.getPaint().getFontMetricsInt(), LayoutCreator .dp(14), false));
        valueTextView.setText(value);
        needDivider = divider;
        setWillNotDraw(!divider);
    }

    @Override
    public void invalidate() {
        super.invalidate();
        textView.invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (needDivider && Theme.dividerPaint != null) {
            canvas.drawLine(G.isAppRtl  ? 0 : LayoutCreator .dp(imageView.getVisibility() == VISIBLE ? 71 : 20), getMeasuredHeight() - 1, getMeasuredWidth() - (G.isAppRtl  ? LayoutCreator .dp(imageView.getVisibility() == VISIBLE ? 71 : 20) : 0), getMeasuredHeight() - 1, Theme.dividerPaint);
        }
    }

    public void setTextColor(int color) {
        textView.setTextColor(color);
        valueTextView.setTextColor(Theme.getColor(Theme.key_title_text));
        invalidate();
    }
}
