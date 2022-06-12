package net.iGap.messenger.ui.cell;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;

import net.iGap.helper.LayoutCreator;

public class SolidWallpaperCell extends CardView {
    public SolidWallpaperCell(@NonNull Context context) {
        super(context);
        setCardElevation(4);
        setUseCompatPadding(true);
    }

    public void setCardBackgroundColor(int color) {
        setCardBackgroundColor(color);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(LayoutCreator.dp(80), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(LayoutCreator.dp(100), MeasureSpec.EXACTLY));
    }
}


