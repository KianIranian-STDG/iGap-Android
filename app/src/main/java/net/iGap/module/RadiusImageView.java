package net.iGap.module;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatImageView;

import net.iGap.R;
import net.iGap.adapter.items.chat.ViewMaker;

public class RadiusImageView extends AppCompatImageView {

    private float radius = ViewMaker.i_Dp(R.dimen.dp10);
    private boolean radiusRightSide = true;

    public RadiusImageView(Context context) {
        super(context);
    }

    public RadiusImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initRadiusSize(attrs);
    }

    private void initRadiusSize(AttributeSet attrs) {

        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.RadiusImageView);
        radius = typedArray.getDimension(R.styleable.RadiusImageView_riv_radius, radius);
        radiusRightSide = typedArray.getBoolean(R.styleable.RadiusImageView_riv_radius_right_side, true);
        typedArray.recycle();

    }

    public RadiusImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //set corner radius to image view
        final float[] finalRadius = new float[8];
        Path clipPath = new Path();
        RectF rect = new RectF(0, 0, this.getWidth(), this.getHeight());

        finalRadius[0] = radius;
        finalRadius[1] = radius;
        finalRadius[2] = radius;
        finalRadius[3] = radius;
        finalRadius[4] = radius;
        finalRadius[5] = radius;
        finalRadius[6] = radius;
        finalRadius[7] = radius;

        if (!radiusRightSide) {
            finalRadius[2] = 0;
            finalRadius[3] = 0;
            finalRadius[4] = 0;
            finalRadius[5] = 0;
        }

        clipPath.addRoundRect(rect, finalRadius, Path.Direction.CW);
        canvas.clipPath(clipPath);
        super.onDraw(canvas);
    }
}
