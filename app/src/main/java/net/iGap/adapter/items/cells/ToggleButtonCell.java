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
import net.iGap.module.CustomToggleButton;
import net.iGap.module.Theme;

public class ToggleButtonCell extends FrameLayout {
    private CustomToggleButton toggleButton;
    private TextView textView;

    private boolean needDivider;
    private boolean isRtl = G.isAppRtl;

    public ToggleButtonCell(@NonNull Context context, boolean needDivider) {
        this(context, needDivider, 21);
    }

    public ToggleButtonCell(@NonNull Context context, boolean needDivider, int padding) {
        super(context);

        this.needDivider = needDivider;
        setWillNotDraw(!needDivider);

        textView = new TextView(getContext());
        textView.setText("Salam Bar Man");
        textView.setTextColor(Theme.getInstance().getTitleTextColor(getContext()));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        textView.setTypeface(ResourcesCompat.getFont(getContext(), R.font.main_font));
        textView.setLines(1);
        textView.setMaxLines(1);
        textView.setSingleLine(true);
        textView.setEllipsize(TextUtils.TruncateAt.END);
        textView.setGravity((isRtl ? Gravity.RIGHT : Gravity.LEFT) | Gravity.CENTER_VERTICAL);
        addView(textView, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.MATCH_PARENT, Gravity.CENTER_VERTICAL | (isRtl ? Gravity.RIGHT : Gravity.LEFT), isRtl ? 72 : padding, 0, isRtl ? padding : 72, 0));

        toggleButton = new CustomToggleButton(getContext());
        addView(toggleButton, LayoutCreator.createFrame(LayoutCreator.WRAP_CONTENT, LayoutCreator.WRAP_CONTENT, Gravity.CENTER_VERTICAL | (isRtl ? Gravity.LEFT : Gravity.RIGHT), isRtl ? 24 : 0, 0, isRtl ? 0 : 24, 0));

    }

    public void setText(CharSequence text) {
        textView.setText(text);
    }

    public boolean isChecked() {
        return toggleButton.isChecked();
    }

    @Override
    public void invalidate() {
        super.invalidate();
        textView.invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (needDivider) {
            canvas.drawLine(isRtl ? 0 : LayoutCreator.dp(20), getMeasuredHeight() - 1, getMeasuredWidth() - (isRtl ? LayoutCreator.dp(20) : 0), getMeasuredHeight() - 1, Theme.getInstance().getDividerPaint(getContext()));
        }
    }


}
