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

import net.iGap.G;
import net.iGap.R;
import net.iGap.helper.LayoutCreator;
import net.iGap.messenger.theme.Theme;

public class LanguageCell extends FrameLayout {
    private TextView textView;
    private TextView textView2;
    private ImageView checkImage;
    private boolean needDivider;

    public LanguageCell(Context context) {
        super(context);

        setWillNotDraw(false);

        textView = new TextView(context);
        textView.setTextColor(Theme.getColor(Theme.key_default_text));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        textView.setLines(1);
        textView.setMaxLines(1);
        textView.setSingleLine(true);
        textView.setEllipsize(TextUtils.TruncateAt.END);
        textView.setGravity((G.isAppRtl ? Gravity.RIGHT : Gravity.LEFT) | Gravity.TOP);
        addView(textView, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.MATCH_PARENT, (G.isAppRtl ? Gravity.RIGHT : Gravity.LEFT) | Gravity.TOP, G.isAppRtl ? 23 + 48 : 23, 4, G.isAppRtl ? 23 : 23 + 48, 0));

        textView2 = new TextView(context);
        textView2.setTextColor(Theme.getColor(Theme.key_title_text));
        textView2.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
        textView2.setLines(1);
        textView2.setMaxLines(1);
        textView2.setSingleLine(true);
        textView2.setEllipsize(TextUtils.TruncateAt.END);
        textView2.setGravity((G.isAppRtl ? Gravity.RIGHT : Gravity.LEFT) | Gravity.TOP);
        addView(textView2, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.MATCH_PARENT, (G.isAppRtl ? Gravity.RIGHT : Gravity.LEFT) | Gravity.TOP, G.isAppRtl ? 23 + 48 : 23, 25, G.isAppRtl ? 23 : 23 + 48, 0));

        checkImage = new ImageView(context);
        checkImage.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_icon), PorterDuff.Mode.MULTIPLY));
        checkImage.setImageResource(R.drawable.sticker_added);
        addView(checkImage, LayoutCreator.createFrame(19, 14, (G.isAppRtl ? Gravity.LEFT : Gravity.RIGHT) | Gravity.CENTER_VERTICAL, 23, 0, 23, 0));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(LayoutCreator.dp(50) + (needDivider ? 1 : 0), MeasureSpec.EXACTLY));
    }

    public void setLanguage(String language, String desc, boolean divider) {
        textView.setText(desc != null ? desc : language);
        textView2.setText(language);
        needDivider = divider;
    }

    public void setValue(String name, String nameEnglish) {
        textView.setText(name);
        textView2.setText(nameEnglish);
        checkImage.setVisibility(INVISIBLE);
        needDivider = false;
    }


    public void setLanguageSelected(boolean value) {
        checkImage.setVisibility(value ? VISIBLE : INVISIBLE);
    }

    public void setTextColor(int color) {
        textView.setTextColor(color);
        textView2.setTextColor(Theme.getColor(Theme.key_title_text));
        checkImage.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_icon), PorterDuff.Mode.MULTIPLY));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (needDivider) {
            canvas.drawLine(G.isAppRtl ? 0 : LayoutCreator.dp(20), getMeasuredHeight() - 1, getMeasuredWidth() - (G.isAppRtl ? LayoutCreator.dp(20) : 0), getMeasuredHeight() - 1, Theme.dividerPaint);
        }
    }
}
