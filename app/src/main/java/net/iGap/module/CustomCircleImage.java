/*
 * This is the source code of iGap for Android
 * It is licensed under GNU AGPL v3.0
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright © 2017 , iGap - www.iGap.net
 * iGap Messenger | Free, Fast and Secure instant messaging application
 * The idea of the Kianiranian Company - www.kianiranian.com
 * All rights reserved.
 */

package net.iGap.module;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import net.iGap.R;
import net.iGap.messenger.theme.Theme;

/**
 * circle button for indicate current page in fragment Introduce
 */

public class CustomCircleImage extends View {

    private final int CIRCLE_RADIUS = (int) getResources().getDimension(R.dimen.dp12);
    private final int CIRCLE_SPACE = (int) getResources().getDimension(R.dimen.dp16);
    private Paint fiiPain;
    private Paint strokePain;
    private int count;
    private float percent;
    private int screenWidth;
    private int circleWidth;
    private int current;
    private int position;
    private float offsetX;
    private float tt = getResources().getDimension(R.dimen.dp16);

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
        fiiPain.setColor(Theme.getColor(Theme.key_theme_color));
        fiiPain.setAntiAlias(true);

        strokePain = new Paint();
        strokePain.setStyle(Paint.Style.FILL);
        strokePain.setColor(Theme.getColor(Theme.key_line));
        strokePain.setAntiAlias(true);

        screenWidth = getResources().getDisplayMetrics().widthPixels;
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
                }
            }

            if (fillCheck) {
                canvas.drawCircle(offsetX + i * (CIRCLE_RADIUS + CIRCLE_SPACE), tt, CIRCLE_RADIUS / 2, fiiPain);
            }
        }
    }
}
