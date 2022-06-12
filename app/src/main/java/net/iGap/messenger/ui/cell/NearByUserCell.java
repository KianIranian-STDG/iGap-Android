package net.iGap.messenger.ui.cell;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;

import net.iGap.G;
import net.iGap.R;
import net.iGap.helper.LayoutCreator;
import net.iGap.messenger.theme.Theme;
import net.iGap.module.CircleImageView;

public class NearByUserCell extends FrameLayout {

    private CircleImageView avatarImageView;
    private TextView nameTextView;
    private TextView statusTextView;

    private boolean needDivider;
    private boolean isRTL = G.isAppRtl;
    private int padding = 16;

    public NearByUserCell(Context context) {
        super(context);

        avatarImageView = new CircleImageView(context);
        addView(avatarImageView, LayoutCreator.createFrame(46, 46, (isRTL ? Gravity.RIGHT : Gravity.LEFT) | Gravity.CENTER_VERTICAL, isRTL ? 0 : padding, 0, isRTL ? padding : 0, 0));

        nameTextView = new TextView(context);
        nameTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
        nameTextView.setTypeface(ResourcesCompat.getFont(context, R.font.main_font), Typeface.BOLD);
        nameTextView.setGravity((isRTL ? Gravity.RIGHT : Gravity.LEFT) | Gravity.TOP);
        addView(nameTextView, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.WRAP_CONTENT, (isRTL ? Gravity.RIGHT : Gravity.LEFT) | Gravity.TOP, (56 + padding), 10, (56 + padding), 0));

        statusTextView = new TextView(context);
        statusTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
        statusTextView.setTypeface(ResourcesCompat.getFont(context, R.font.main_font));
        statusTextView.setGravity((isRTL ? Gravity.RIGHT : Gravity.LEFT) | Gravity.TOP);
        addView(statusTextView, LayoutCreator.createFrame(LayoutCreator.MATCH_PARENT, LayoutCreator.WRAP_CONTENT, (isRTL ? Gravity.RIGHT : Gravity.LEFT) | Gravity.TOP, (56 + padding), 34, (56 + padding), 0));

    }

    public void setValues(String name, String status, int drawable, boolean needDivider) {
        this.needDivider = needDivider;
        nameTextView.setText(name);
        statusTextView.setText(status);
        avatarImageView.setImageDrawable(getContext().getResources().getDrawable(drawable));
        setWillNotDraw(!needDivider);
    }


    public void setValuesColors(int nameColor, int statusColor) {
        nameTextView.setTextColor(nameColor);
        statusTextView.setTextColor(statusColor);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(LayoutCreator.dp(64) + (needDivider ? 1 : 0), MeasureSpec.EXACTLY));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (needDivider) {
            canvas.drawLine(isRTL ? 0 : LayoutCreator.dp(68), getMeasuredHeight() - 1, getMeasuredWidth() - (isRTL ? LayoutCreator.dp(68) : 0), getMeasuredHeight() - 1, Theme.dividerPaint);
        }
    }

    public void setTextColor(int color) {
        nameTextView.setTextColor(color);
        statusTextView.setTextColor(color);
        invalidate();
    }
}
