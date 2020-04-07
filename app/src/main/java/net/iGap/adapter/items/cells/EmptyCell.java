package net.iGap.adapter.items.cells;

import android.content.Context;
import android.view.View;

import net.iGap.helper.LayoutCreator;

public class EmptyCell extends View {

    private int cellHeight;

    public EmptyCell(Context context) {
        this(context, 8);
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
