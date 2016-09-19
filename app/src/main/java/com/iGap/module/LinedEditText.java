package com.iGap.module;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.EditText;

import com.iGap.G;
import com.iGap.R;

/**
 * Created by Rahmani on 9/19/2016.
 */
public class LinedEditText extends EditText {

//    private Rect mRect;
//    private Paint mPaint;
//
//    // we need this constructor for LayoutInflater
//    public LinedEditText(Context context, AttributeSet attrs) {
//        super(context, attrs);
//
//        mRect = new Rect();
//        mPaint = new Paint();
//        mPaint.setStyle(Paint.Style.STROKE);
//        mPaint.setColor(0x800000FF);
//    }
//
//    @Override
//    protected void onDraw(Canvas canvas) {
//        int count = getLineCount();
//        Rect r = mRect;
//        Paint paint = mPaint;
//
//        for (int i = 0; i < count; i++) {
//            int baseline = getLineBounds(i, r);
//            canvas.drawLine(r.left, baseline + 1, r.right, baseline + 1, paint);
//        }
//
//        super.onDraw(canvas);
//    }


    private Rect mRect;
    private Paint mPaint;

    // we need this constructor for LayoutInflater
    public LinedEditText(Context context, AttributeSet attrs) {
        super(context, attrs);

        mRect = new Rect();
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaint.setColor(G.context.getResources().getColor(R.color.toolbar_background)); //SET YOUR OWN COLOR HERE
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //int count = getLineCount();

        int height = getHeight();
        int line_height = getLineHeight();

        int count = height / line_height;

        if (getLineCount() > count)
            count = getLineCount();//for long text with scrolling

        Rect r = mRect;
        Paint paint = mPaint;
        int baseline = getLineBounds(0, r);//first line

        for (int i = 0; i < count; i++) {

            canvas.drawLine(r.left, baseline + 1, r.right, baseline + 1, paint);
            baseline += getLineHeight();//next line
        }

        super.onDraw(canvas);
    }
}
