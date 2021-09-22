package net.iGap.story.liststories.cells;

import android.content.Context;
import android.graphics.Canvas;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;

import net.iGap.G;
import net.iGap.R;
import net.iGap.helper.LayoutCreator;
import net.iGap.module.Theme;

public class CheckBoxCell extends FrameLayout {

    private final CheckBox checkBox;
    private final TextView textView;

    private final boolean isRtl = G.isAppRtl;
    private boolean needDivider;

    public CheckBoxCell(Context context, boolean needDivider) {
        super(context);
        this.needDivider = needDivider;
        setWillNotDraw(false);

        checkBox = new CheckBox(context);
        checkBox.setAlpha(1f);
        addView(checkBox);

        textView = new TextView(context);
        textView.setGravity(isRtl ? Gravity.RIGHT : Gravity.LEFT);
        textView.setPadding(0, 0, 0, LayoutCreator.dp(1));
        textView.setText("Name");
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        textView.setTypeface(ResourcesCompat.getFont(context, R.font.main_font));
        textView.setSingleLine();
        addView(textView);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        checkBox.measure(MeasureSpec.makeMeasureSpec(LayoutCreator.dp(25), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(LayoutCreator.dp(25), MeasureSpec.EXACTLY));
        textView.measure(MeasureSpec.makeMeasureSpec((MeasureSpec.getSize(widthMeasureSpec) - checkBox.getMeasuredWidth() - LayoutCreator.dp(16)), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(LayoutCreator.dp(52) - LayoutCreator.dp(16), MeasureSpec.EXACTLY));
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), LayoutCreator.dp(52));
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {

        int checkBoxTop = (getMeasuredHeight() - checkBox.getMeasuredHeight()) / 2;
        int checkBoxBottom = checkBoxTop + checkBox.getMeasuredHeight();
        int checkBoxLeft = isRtl ? getMeasuredWidth() - checkBox.getMeasuredWidth() - LayoutCreator.dp(8) : LayoutCreator.dp(8);
        int checkBoxRight = isRtl ? getMeasuredWidth() - LayoutCreator.dp(8) : LayoutCreator.dp(8) + checkBox.getMeasuredWidth();
        checkBox.layout(checkBoxLeft, checkBoxTop, checkBoxRight, checkBoxBottom);

        int textViewBottom = checkBoxTop + textView.getMeasuredHeight();
        int textViewLeft = isRtl ? checkBoxLeft - textView.getMeasuredWidth() - LayoutCreator.dp(8) : checkBoxRight + LayoutCreator.dp(8);
        int textViewRight = isRtl ? checkBoxLeft - LayoutCreator.dp(8) : textViewLeft + textView.getMeasuredWidth();
        textView.layout(textViewLeft, checkBoxTop, textViewRight, textViewBottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (needDivider)
            canvas.drawLine(isRtl ? 0 : LayoutCreator.dp(21), getMeasuredHeight() - 1, getMeasuredWidth() - (isRtl ? LayoutCreator.dp(21) : 0), getMeasuredHeight() - 1, Theme.getInstance().getDividerPaint(getContext()));
        super.onDraw(canvas);
    }

    public void setText(String string) {
        textView.setText(string);
    }

    public void setChecked(boolean checked){
        checkBox.setChecked(checked);
    }
}
