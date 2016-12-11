package com.iGap.module;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;

import com.makeramen.roundedimageview.RoundedImageView;

/**
 * An image view which retains the aspect ratio of the image (makes width match
 * height)
 */
public class ReserveSpaceRoundedImageView extends RoundedImageView {
    private int reservedWidth = 0;
    private int reservedHeight = 0;

    public void reserveSpace(float width, float height) {
        final int[] dimens = AndroidUtils.scaleDimenWithSavedRatio(getContext(), width, height);
        Bitmap bitmap = Bitmap.createBitmap(dimens[0], dimens[1], Bitmap.Config.ARGB_4444);
        setImageBitmap(bitmap);

        this.reservedWidth = dimens[0];
        this.reservedHeight = dimens[1];
    }

    public ReserveSpaceRoundedImageView(Context context) {
        super(context);
    }

    public ReserveSpaceRoundedImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        setMeasuredDimension(reservedWidth, reservedHeight);
    }
}