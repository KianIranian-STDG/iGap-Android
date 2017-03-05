package com.iGap.module;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import com.iGap.proto.ProtoGlobal;
import com.makeramen.roundedimageview.RoundedImageView;

/**
 * An image view which retains the aspect ratio of the image (makes width match
 * height)
 */
public class ReserveSpaceRoundedImageView extends RoundedImageView {
    private int reservedWidth = 0;
    private int reservedHeight = 0;

    public int[] reserveSpace(float width, float height, ProtoGlobal.Room.Type roomType) {
        final int[] dimens = AndroidUtils.scaleDimenWithSavedRatio(getContext(), width, height, roomType);
        if (dimens[0] != 0 && dimens[1] != 0) {
            Bitmap bitmap = Bitmap.createBitmap(dimens[0], dimens[1], Bitmap.Config.ARGB_4444);
            setImageBitmap(bitmap);

            this.reservedWidth = dimens[0];
            this.reservedHeight = dimens[1];
            return dimens;
        }
        return new int[]{0, 0};
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