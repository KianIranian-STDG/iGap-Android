package net.iGap.adapter.items.cells;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;

import net.iGap.G;
import net.iGap.R;
import net.iGap.helper.LayoutCreator;
import net.iGap.module.CustomToggleButton;
import net.iGap.module.Theme;

@SuppressLint("ViewConstructor")
public class ToggleButtonCell extends FrameLayout {
    private CustomToggleButton toggleButton;
    private TextView textView;
    private CompoundButton.OnCheckedChangeListener checkedChangeListener;

    private boolean needDivider;
    private boolean isRtl = G.isAppRtl;

    public ToggleButtonCell(@NonNull Context context) {
        this(context, false, 21);
    }

    public ToggleButtonCell(@NonNull Context context, boolean needDivider) {
        this(context, needDivider, 21);
    }

    public ToggleButtonCell(@NonNull Context context, boolean needDivider, int padding) {
        super(context);

        this.needDivider = needDivider;
        setWillNotDraw(!needDivider);

        textView = new TextView(getContext());
        textView.setTextColor(Theme.getInstance().getTitleTextColor(getContext()));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        textView.setTypeface(ResourcesCompat.getFont(getContext(), R.font.main_font));
        textView.setLines(1);
        textView.setMaxLines(1);
        textView.setSingleLine(true);
        textView.setEllipsize(TextUtils.TruncateAt.END);
        textView.setGravity((isRtl ? Gravity.RIGHT : Gravity.LEFT) | Gravity.CENTER_VERTICAL);
        addView(textView, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.MATCH_PARENT, Gravity.CENTER_VERTICAL | (isRtl ? Gravity.RIGHT : Gravity.LEFT), isRtl ? 72 : padding, 0, isRtl ? padding : 72, 0));

        toggleButton = new CustomToggleButton(getContext());
        toggleButton.setClickable(true);
        toggleButton.setGravity(Gravity.CENTER);
        addView(toggleButton, LayoutCreator.createFrame(LayoutCreator.WRAP_CONTENT, LayoutCreator.WRAP_CONTENT, Gravity.CENTER_VERTICAL | (isRtl ? Gravity.LEFT : Gravity.RIGHT), isRtl ? 24 : 0, 0, isRtl ? 0 : 24, 0));

        setOnClickListener(v -> toggleButton.setChecked(!toggleButton.isChecked()));

        toggleButton.setOnClickListener(v -> {
            toggleButton.setChecked(!toggleButton.isChecked());
            performClick();
        });

        toggleButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (checkedChangeListener != null)
                checkedChangeListener.onCheckedChanged(buttonView, isChecked);

        });

    }

    public void setTextAndCheck(String text, boolean checked, boolean needDivider) {
        textView.setText(text);
        toggleButton.setChecked(checked);
        this.needDivider = needDivider;
        invalidate();
    }

    public void setTextAndCheckEnable(String text, boolean checked, boolean enable, boolean needDivider) {
        textView.setText(text);
        toggleButton.setChecked(checked && enable);
        setEnabled(enable);
        this.needDivider = needDivider;
        invalidate();
    }

    public void setText(String text, boolean needDivider) {
        textView.setText(text);
        this.needDivider = needDivider;
        invalidate();
    }

    public boolean isChecked() {
        return toggleButton.isChecked();
    }

    public void setChecked(boolean checked) {
        toggleButton.setChecked(checked);
    }

    @Override
    public void invalidate() {
        super.invalidate();
        textView.invalidate();
    }

    @Override
    public void setEnabled(boolean enabled) {
        toggleButton.setEnabled(enabled);
        textView.setTextColor(enabled ? Theme.getInstance().getTitleTextColor(getContext()) : Theme.getInstance().getSubTitleColor(getContext()));
        super.setEnabled(enabled);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (needDivider) {
            canvas.drawLine(isRtl ? 0 : LayoutCreator.dp(20), getMeasuredHeight() - 1, getMeasuredWidth() - (isRtl ? LayoutCreator.dp(20) : 0), getMeasuredHeight() - 1, Theme.getInstance().getDividerPaint(getContext()));
        }
    }

    public void setOnCheckedChangeListener(@Nullable CompoundButton.OnCheckedChangeListener checkedChangeListener) {
        this.checkedChangeListener = checkedChangeListener;
    }
}
