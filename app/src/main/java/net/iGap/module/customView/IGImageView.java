package net.iGap.module.customView;

import android.content.Context;
import android.graphics.Canvas;
import android.view.View;

import net.iGap.helper.ImageObserver;

public class IGImageView extends View {
    private ImageObserver imageObserver;

    public IGImageView(Context context) {
        super(context);
        imageObserver = new ImageObserver(this);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        imageObserver.onDraw(canvas, 0, 0, getWidth(), getHeight());
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        imageObserver.onDetachedFromWindow();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        imageObserver.onAttachedToWindow();
    }
}
