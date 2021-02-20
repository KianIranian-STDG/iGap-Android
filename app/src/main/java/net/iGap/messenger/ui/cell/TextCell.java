package net.iGap.messenger.ui.cell;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.View;

import androidx.core.content.res.ResourcesCompat;

import net.iGap.G;
import net.iGap.R;
import net.iGap.helper.LayoutCreator;

@SuppressLint("ViewConstructor")
public class TextCell extends View {

    private StaticLayout textLayout;
    private TextPaint textPaint;
    private CharSequence lastText;
    private int textWidth;
    private int textHeight;
    private int textX;
    private int textY;

    private StaticLayout iconLayout;
    private TextPaint iconPaint;
    private int iconX;
    private int iconY;
    private int iconWidth;
    private int iconHeight;
    private int iconRes;

    private int startPadding = LayoutCreator.dp(16);

    private int layoutHeight;
    private int layoutWidth;

    private int height = LayoutCreator.dp(50);

    private boolean isRtl = G.isAppRtl;

    private boolean needDivider;

    public TextCell(Context context) {
        super(context);

        textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextSize(LayoutCreator.dp(14));
        textPaint.setTypeface(ResourcesCompat.getFont(context, R.font.main_font));

        iconPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        iconPaint.setTypeface(ResourcesCompat.getFont(getContext(), R.font.font_icon_new));
        iconPaint.setTextSize(LayoutCreator.dp(23));
    }


    public TextCell(Context context, boolean needDivider) {
        this(context);
        this.needDivider = needDivider;
    }

    public void setText(CharSequence text) {
        setText(text, false);
    }

    public void setText(CharSequence cellText, boolean needDivider) {
        this.needDivider = needDivider;
        if (lastText != cellText) {
            lastText = cellText;
            createTextLayout();
            invalidate();
        }
    }

    public void setIconRes(int iconRes) {
        if (this.iconRes != iconRes) {
            this.iconRes = iconRes;
            createIconLayout();
            invalidate();
        }
    }

    public void setTextColor(int color) {
        textPaint.setColor(color);
        invalidate();
    }

    public void setIconColor(int color) {
        iconPaint.setColor(color);
        invalidate();
    }

    private void createTextLayout() {
        textWidth = (int) textPaint.measureText(lastText.toString());
        CharSequence text = TextUtils.ellipsize(lastText, textPaint, textWidth * 1.5f, TextUtils.TruncateAt.END);
        textLayout = new StaticLayout(text, textPaint, textWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
        textHeight = textLayout.getHeight();
    }

    private void createIconLayout() {
        String icon = getResources().getString(iconRes);
        iconWidth = (int) iconPaint.measureText(icon);
        iconLayout = new StaticLayout(icon, iconPaint, iconWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
        iconHeight = iconLayout.getHeight();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), height + (needDivider ? 1 : 0));
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        layoutHeight = bottom - top;
        layoutWidth = right - left;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int cy = layoutHeight / 2;

        if (iconLayout != null) {
            iconY = cy - (iconHeight / 2);
            iconX = isRtl ? layoutWidth - iconWidth - startPadding : startPadding;
            canvas.save();
            canvas.translate(iconX, iconY);
            iconLayout.draw(canvas);
            canvas.restore();
        }

        if (textLayout != null) {
            textY = cy - (textHeight / 2);
            textX = isRtl ? (iconX != 0 ? iconX - textWidth - LayoutCreator.dp(12) : layoutWidth - textWidth - startPadding) : (iconX != 0 ? iconX + iconWidth + LayoutCreator.dp(12) : startPadding);

            canvas.save();
            canvas.translate(textX, textY);
            textLayout.draw(canvas);
            canvas.restore();
        }

        if (needDivider) {
            canvas.drawLine(isRtl ? 6 : textX - 4, layoutHeight - 1, iconX != 0 ? (isRtl ? textX + textWidth : layoutWidth) : (isRtl ? textX + textWidth : layoutWidth), layoutHeight - 1, net.iGap.module.Theme.getInstance().getDividerPaint(getContext()));
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        textPaint.setAlpha(enabled ? 255 : 125);
        iconPaint.setAlpha(enabled ? 255 : 125);
        super.setEnabled(enabled);
    }
}
