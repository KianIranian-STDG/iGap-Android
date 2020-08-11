package net.iGap.adapter.items.chat;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;

import net.iGap.R;
import net.iGap.helper.HelperCalander;
import net.iGap.libs.bottomNavigation.Util.Utils;

import static net.iGap.libs.bottomNavigation.Util.Utils.setTextSize;

public class BadgeView extends FrameLayout {
    private TextView textView;
    private int badgeColor;

    public BadgeView(Context context) {
        super(context);
        init();
    }

    public BadgeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BadgeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public BadgeView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        textView = new TextView(getContext());
        textView.setGravity(Gravity.CENTER);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(Utils.dpToPx(6), 0, Utils.dpToPx(6), 0);
        textView.setLayoutParams(params);
        textView.setTextColor(getContext().getResources().getColor(R.color.white));
        setTextSize(textView, R.dimen.dp10);
        addView(textView);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        paint.setColor(badgeColor);

        Path path = new Path();
        path.addRoundRect(new RectF(0, 0, getWidth(), getHeight()), 30, 30, Path.Direction.CCW);
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

    public void setTextView(TextView textView) {
        this.textView = textView;
    }

    public int getBadgeColor() {
        return badgeColor;
    }

    public void setBadgeColor(int badgeColor) {
        this.badgeColor = badgeColor;
    }

}
