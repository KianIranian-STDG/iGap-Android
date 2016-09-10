package com.iGap.module;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

import com.iGap.G;
import com.iGap.R;

public class CustomCircleImage extends ImageView {

    private Paint fiiPain;
    private Paint strokePain;
    private int count;
    private float percent;
    private int screenWidth;
    private int circleWidth;
    private int current;
    private int position;
    private static final int CIRCLE_RADIUS = (int) G.context.getResources().getDimension(R.dimen.dp12);
    private static final int CIRCLE_SPACE = (int) G.context.getResources().getDimension(R.dimen.dp16);
    private float offsetX;
    private float tt = G.context.getResources().getDimension(R.dimen.dp16);


    public CustomCircleImage(Context context) {
        super(context);
        initialize();
    }

    public CustomCircleImage(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();

    }

    public CustomCircleImage(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
    }

    private void initialize() {

        fiiPain = new Paint();
        fiiPain.setStyle(Paint.Style.FILL);
        fiiPain.setColor(Color.parseColor(("#31BCB6")));
        fiiPain.setAntiAlias(true);

        strokePain = new Paint();
        strokePain.setStyle(Paint.Style.STROKE);
        strokePain.setColor(Color.parseColor(("#31BCB6")));
        strokePain.setAntiAlias(true);

        screenWidth = G.context.getResources().getDisplayMetrics().widthPixels;
    }

    private void width() {

        circleWidth = count * (CIRCLE_RADIUS + CIRCLE_SPACE);

        offsetX = (screenWidth - circleWidth) / 2;
    }

    public void circleButtonCount(int count) {
        this.count = count;
        width();
    }

    public void percentScroll(float positionOffset, int position) {

        percent = positionOffset;
        this.position = position;
        postInvalidate();

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        for (int i = 0; i < count; i++) {


            canvas.drawCircle(offsetX + i * (CIRCLE_RADIUS + CIRCLE_SPACE), tt, CIRCLE_RADIUS / 2, strokePain);
            boolean fillCheck = false;
            if (i == position) {
                fiiPain.setAlpha((int) ((1.0f - percent) * 255));
                fillCheck = true;
            }

            if (percent > 0) {
                if (i == position + 1) {
                    fiiPain.setAlpha((int) (percent * 255));
                    fillCheck = true;
                    Log.i("TAG", "i: " + i);
                }
            }

            if (fillCheck) {
                canvas.drawCircle(offsetX + i * (CIRCLE_RADIUS + CIRCLE_SPACE), tt, CIRCLE_RADIUS / 2, fiiPain);
            }
        }
    }
}
