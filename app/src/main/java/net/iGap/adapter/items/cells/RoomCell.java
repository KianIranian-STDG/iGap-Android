package net.iGap.adapter.items.cells;

import android.content.Context;
import android.graphics.Canvas;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.view.View;

import net.iGap.helper.ImageObserver;

public class RoomCell extends View {
    private ImageObserver avatarImage;

    private StaticLayout nameLayout;
    private TextPaint textPaint;
    private int nameX;
    private int nameY;
    private int nameWidth;
    private int nameHeight;

    public RoomCell(Context context) {
        super(context);


    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }
}
