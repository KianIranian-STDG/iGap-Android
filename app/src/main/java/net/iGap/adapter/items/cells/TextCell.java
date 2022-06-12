package net.iGap.adapter.items.cells;

import android.content.Context;
import android.graphics.Canvas;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;

import net.iGap.G;
import net.iGap.R;
import net.iGap.helper.LayoutCreator;
import net.iGap.messenger.theme.Theme;

public class TextCell extends FrameLayout {
    private TextView textView;

    private boolean needDivider;
    private boolean isRtl = G.isAppRtl;

    public TextCell(@NonNull Context context) {
        this(context, false, 22);
    }

    public TextCell(@NonNull Context context, boolean needDivider) {
        this(context, needDivider, 22);
    }

    public TextCell(@NonNull Context context, boolean needDivider, int padding) {
        super(context);

        this.needDivider = needDivider;
        setWillNotDraw(!needDivider);

        textView = new TextView(getContext());
        textView.setTextColor(Theme.getColor(Theme.key_title_text));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        textView.setTypeface(ResourcesCompat.getFont(getContext(), R.font.main_font));
        textView.setLines(1);
        textView.setMaxLines(1);
        textView.setSingleLine(true);
        textView.setEllipsize(TextUtils.TruncateAt.END);
        textView.setGravity((isRtl ? Gravity.RIGHT : Gravity.LEFT) | Gravity.CENTER_VERTICAL);
        addView(textView, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.MATCH_PARENT, Gravity.CENTER_VERTICAL | (isRtl ? Gravity.RIGHT : Gravity.LEFT), isRtl ? 8 : padding, 0, isRtl ? padding : 8, 0));
    }

    public void setValue(CharSequence text) {
        textView.setText(text);
    }

    public void setTextColor(int color) {
        textView.setTextColor(color);
        invalidate();
    }

    @Override
    public void invalidate() {
        super.invalidate();
        textView.invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (needDivider) {
            canvas.drawLine(isRtl ? 0 : LayoutCreator.dp(21), getMeasuredHeight() - 1, getMeasuredWidth() - (isRtl ? LayoutCreator.dp(21) : 0), getMeasuredHeight() - 1,Theme.dividerPaint);
        }
    }
}
