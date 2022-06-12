package net.iGap.messenger.ui.cell;

import android.content.Context;
import android.widget.FrameLayout;

import net.iGap.helper.LayoutCreator;

public class EmptyCell extends FrameLayout {

    private int cellHeight;

    public EmptyCell(Context context) {
        this(context, 16);
    }

    public EmptyCell(Context context, int height) {
        super(context);
        cellHeight = height;
    }

    public void setHeight(int height) {
        if (cellHeight != height) {
            cellHeight = height;
            requestLayout();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(LayoutCreator.dp(cellHeight), MeasureSpec.EXACTLY));
    }
}
