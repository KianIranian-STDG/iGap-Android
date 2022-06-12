package net.iGap.messenger.ui.cell;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.text.TextPaint;
import android.view.Gravity;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;

import net.iGap.helper.HelperCalander;
import net.iGap.helper.LayoutCreator;
import net.iGap.messenger.SharedConfig;
import net.iGap.messenger.theme.Theme;
import net.iGap.messenger.ui.components.SeekBarView;

public class TextSizeCell extends FrameLayout {

    public ThemePreviewMessagesCell messagesCell;
    public SeekBarView sizeBar;
    private int startFontSize = 12;
    private int endFontSize = 30;

    private TextPaint textPaint;
    private int lastWidth;

    public TextSizeCell(Context context) {
        super(context);

        setWillNotDraw(false);

        textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextSize(LayoutCreator.dp(16));

        sizeBar = new SeekBarView(context);
        sizeBar.setReportChanges(true);
        sizeBar.setImportantForAccessibility(IMPORTANT_FOR_ACCESSIBILITY_NO);
        addView(sizeBar, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, 38, Gravity.LEFT | Gravity.TOP, 5, 5, 39, 0));

        messagesCell = new ThemePreviewMessagesCell(context);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            messagesCell.setImportantForAccessibility(IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS);
        }
        addView(messagesCell, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.WRAP_CONTENT, Gravity.LEFT | Gravity.TOP, 0, 53, 0, 0));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        textPaint.setColor(Theme.getColor(Theme.key_theme_color));
        canvas.drawText(checkPersianNumber("" + SharedConfig.fontSize), getMeasuredWidth() - LayoutCreator.dp(39), LayoutCreator.dp(28), textPaint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        if (lastWidth != width) {
            sizeBar.setProgress((SharedConfig.fontSize - startFontSize) / (float) (endFontSize - startFontSize));
            lastWidth = width;
        }
    }

    @Override
    public void invalidate() {
        super.invalidate();
        messagesCell.invalidate();
        sizeBar.invalidate();
    }

    @Override
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        sizeBar.getSeekBarAccessibilityDelegate().onInitializeAccessibilityNodeInfoInternal(this, info);
    }

    @Override
    public boolean performAccessibilityAction(int action, Bundle arguments) {
        return super.performAccessibilityAction(action, arguments) || sizeBar.getSeekBarAccessibilityDelegate().performAccessibilityActionInternal(this, action, arguments);
    }

    private String checkPersianNumber(String text) {
        if (HelperCalander.isPersianUnicode) {
            return HelperCalander.convertToUnicodeFarsiNumber(text);
        } else {
            return text;
        }
    }

    public void setColors(int color) {
        sizeBar.setColors(color,color);
        messagesCell.setColors(color);
        textPaint.setColor(color);
        messagesCell.setColors(color);
    }
}