package net.iGap.messenger.ui.cell;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;

import net.iGap.G;
import net.iGap.R;
import net.iGap.helper.LayoutCreator;
import net.iGap.messenger.theme.Theme;

public class NotificationLedCell extends FrameLayout {

    private TextView textView;
    private boolean needDivider;
    private Paint paint;

    public NotificationLedCell(Context context) {
        this(context, 21);
    }

    public NotificationLedCell(Context context, int padding) {
        super(context);
        textView = new TextView(context);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        textView.setLines(1);
        textView.setMaxLines(1);
        textView.setSingleLine(true);
        textView.setEllipsize(TextUtils.TruncateAt.END);
        textView.setGravity((G.isAppRtl ? Gravity.RIGHT : Gravity.LEFT) | Gravity.CENTER_VERTICAL);
        textView.setTextColor(Theme.getColor(Theme.key_default_text));
        textView.setTypeface(ResourcesCompat.getFont(context, R.font.main_font));
        addView(textView, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.MATCH_PARENT, (G.isAppRtl ? Gravity.RIGHT : Gravity.LEFT) | Gravity.TOP, padding, 0, padding, 0));
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), LayoutCreator.dp(50) + (needDivider ? 1 : 0));
        int availableWidth = getMeasuredWidth() - getPaddingLeft() - getPaddingRight() - LayoutCreator.dp(34);
        int width = availableWidth / 2;
        textView.measure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(getMeasuredHeight(), MeasureSpec.EXACTLY));
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawArc(new RectF(G.isAppRtl ? LayoutCreator.dp(22) : getMeasuredWidth() - LayoutCreator.dp(40), LayoutCreator.dp(16), G.isAppRtl ? LayoutCreator.dp(40) : getMeasuredWidth() - LayoutCreator.dp(22), LayoutCreator.dp(34)), 0, 360, true, paint);
        if (needDivider) {
            canvas.drawLine(G.isAppRtl ? 0 : LayoutCreator.dp(20), getMeasuredHeight() - 1, getMeasuredWidth() - (G.isAppRtl ? LayoutCreator.dp(20) : 0), getMeasuredHeight() - 1, Theme.dividerPaint);
        }
    }

    @Override
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setEnabled(isEnabled());
    }

    public void setTextAndColor(String text, int color, boolean divider) {
        textView.setText(text);
        paint.setColor(color);
        invalidate();
        needDivider = divider;
        setWillNotDraw(!divider);
    }

    public void setColor(int color) {
        paint.setColor(color);
    }

    public void setTextColor(int color) {
        textView.setTextColor(color);
        invalidate();
    }
}
