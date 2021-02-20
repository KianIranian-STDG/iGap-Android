package net.iGap.messenger.ui.components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;

public class SearchEditText extends androidx.appcompat.widget.AppCompatEditText {

    private Paint circlePaint;
    private Paint textPaint;
    private long initTime;
    private static final float ANIMATION_LENGTH = 650f;

    public SearchEditText(Context context) {
        super(context);
        init();
    }

    public SearchEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setBackground(null);
        setTextColor(Color.WHITE);
        initTime = System.currentTimeMillis();

        circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        circlePaint.setStyle(Paint.Style.FILL);
        circlePaint.setColor(Color.rgb(223, 225, 220));

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.rgb(223, 225, 220));

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(widthMeasureSpec, MeasureSpec.EXACTLY), heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        long currentTime = System.currentTimeMillis();
        long elapsedTime = currentTime - initTime;
        float circleAlphaRatio = ANIMATION_LENGTH / 255;

        float radius = getMeasuredWidth() * (elapsedTime / ANIMATION_LENGTH);
        int alpha = (int) (elapsedTime / circleAlphaRatio);

        circlePaint.setAlpha(255 - alpha);
        canvas.drawCircle(getMeasuredWidth(), getMeasuredHeight() / 2f, radius, circlePaint);

        if (elapsedTime < ANIMATION_LENGTH) {
            invalidate();
        } else {
            super.onDraw(canvas);
        }
    }
}
