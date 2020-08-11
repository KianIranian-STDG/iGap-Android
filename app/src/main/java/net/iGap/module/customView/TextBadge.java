package net.iGap.module.customView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;

import net.iGap.R;
import net.iGap.helper.HelperCalander;
import net.iGap.helper.LayoutCreator;

import static net.iGap.libs.bottomNavigation.Util.Utils.setTextSize;

public class TextBadge extends FrameLayout {
    private TextView textView;
    private int badgeColor;

    public TextBadge(Context context) {
        super(context);
        init();
    }

    public TextBadge(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TextBadge(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        textView = new TextView(getContext());
        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(getContext().getResources().getColor(R.color.white));
        setTextSize(textView, R.dimen.dp10);
        addView(textView);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        textView.measure(LayoutCreator.manageSpec(LayoutCreator.getTextWidth(textView), MeasureSpec.EXACTLY), LayoutCreator.manageSpec(LayoutCreator.getTextHeight(textView), MeasureSpec.EXACTLY));
        textView.layout(0, 0, getWidth(), getHeight());
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(badgeColor);
        Path path = new Path();
        path.addRoundRect(new RectF(0, 0, getWidth(), getHeight()), getHeight() / 2, getHeight() / 2, Path.Direction.CCW);
        canvas.drawPath(path, paint);
        super.dispatchDraw(canvas);
    }

    public TextView getTextView() {
        return textView;
    }

    public void setText(String text) {
        if (HelperCalander.isPersianUnicode) {
            textView.setText(HelperCalander.convertToUnicodeFarsiNumber(text));
            textView.setTypeface(ResourcesCompat.getFont(textView.getContext(), R.font.main_font));
        } else {
            textView.setText(text);
        }
    }

    public void setBadgeColor(int badgeColor) {
        this.badgeColor = badgeColor;
    }
}
