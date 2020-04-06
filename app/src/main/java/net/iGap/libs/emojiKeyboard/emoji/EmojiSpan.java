package net.iGap.libs.emojiKeyboard.emoji;

import android.graphics.Paint;
import android.text.style.ImageSpan;

import androidx.annotation.NonNull;

import net.iGap.helper.LayoutCreator;

public class EmojiSpan extends ImageSpan {
    private Paint.FontMetricsInt fontMetrics;
    private int size = LayoutCreator.dp(20);

    public EmojiSpan(EmojiManager.EmojiDrawable d, int verticalAlignment, int s, Paint.FontMetricsInt original) {
        super(d, verticalAlignment);
        fontMetrics = original;
        if (original != null) {
            size = Math.abs(fontMetrics.descent) + Math.abs(fontMetrics.ascent);
            if (size == 0) {
                size = LayoutCreator.dp(20);
            }
        }
    }

    public void replaceFontMetrics(Paint.FontMetricsInt newMetrics, int newSize) {
        fontMetrics = newMetrics;
        size = newSize;
    }

    @Override
    public int getSize(@NonNull Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fm) {
        if (fm == null) {
            fm = new Paint.FontMetricsInt();
        }

        if (fontMetrics == null) {
            int sz = super.getSize(paint, text, start, end, fm);

            int offset = LayoutCreator.dp(8);
            int w = LayoutCreator.dp(10);
            fm.top = -w - offset;
            fm.bottom = w - offset;
            fm.ascent = -w - offset;
            fm.leading = 0;
            fm.descent = w - offset;

            return sz;
        } else {
            fm.ascent = fontMetrics.ascent;
            fm.descent = fontMetrics.descent;

            fm.top = fontMetrics.top;
            fm.bottom = fontMetrics.bottom;
            if (getDrawable() != null) {
                getDrawable().setBounds(0, 0, size, size);
            }
            return size;
        }
    }
}
