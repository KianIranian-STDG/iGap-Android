package io.meness.github.messageprogress;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;

import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.github.rahatarmanahmed.cpv.CircularProgressViewListener;

/**
 * Created by Alireza Eskandarpour Shoferi (meNESS) on 10/10/2016.
 */

public class MessageProgress extends FrameLayout implements IMessageProgress, View.OnClickListener, CircularProgressViewListener {
    private Paint mPaint = new Paint();
    private OnMessageProgress mOnMessageProgress;
    private Drawable mProgressFinishedDrawable;

    public MessageProgress(Context context, OnMessageProgress onMessageProgress) {
        super(context);
        this.mOnMessageProgress = onMessageProgress;
        init(context);
    }

    public void withOnMessageProgress(OnMessageProgress onMessageProgress) {
        this.mOnMessageProgress = onMessageProgress;
    }

    public MessageProgress(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MessageProgress(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public MessageProgress(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        setOnClickListener(this);
        // important to set false
        setWillNotDraw(false);

        // init progress background paint
        mPaint.setColor(Color.BLACK);
        mPaint.setAlpha(127);
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);

        // int foreground
        setForegroundGravity(Gravity.CENTER);

        // init progress bar
        CircularProgressView progressBar = new CircularProgressView(context);
        progressBar.setMaxProgress(100);
        progressBar.setProgress(0);
        progressBar.addListener(this);
        progressBar.setIndeterminate(false);
        progressBar.startAnimation();
        addView(progressBar);
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawCircle(getWidth() / 2, getHeight() / 2, getWidth() / 2, mPaint);

        super.draw(canvas);
    }

    @Override
    public void withDrawable(@DrawableRes int res) {
        setForeground(AndroidUtils.getDrawable(getContext(), res));
    }

    @Override
    public void withIndeterminate(boolean b) {
        for (int c = 0; c < getChildCount(); c++) {
            View child = getChildAt(c);
            if (child instanceof CircularProgressView) {
                ((CircularProgressView) child).setIndeterminate(b);
                break;
            }
        }
    }

    @Override
    public void withDrawable(Drawable drawable) {
        setForeground(drawable);
    }

    @Override
    public void withProgress(int i) {
        for (int c = 0; c < getChildCount(); c++) {
            View child = getChildAt(c);
            if (child instanceof CircularProgressView) {
                // while updating progress, make sure progress is visible
                if (child.getVisibility() != VISIBLE) {
                    child.setVisibility(VISIBLE);
                }

                ((CircularProgressView) child).setProgress(i);
                break;
            }
        }
    }

    @Override
    public float getProgress() {
        for (int c = 0; c < getChildCount(); c++) {
            View child = getChildAt(c);
            if (child instanceof CircularProgressView) {
                return ((CircularProgressView) child).getProgress();
            }
        }
        return -1;
    }

    @Override
    public void withProgressFinishedDrawable(@DrawableRes int d) {
        mProgressFinishedDrawable = AndroidUtils.getDrawable(getContext(), d);
    }

    @Override
    public void withProgressFinishedDrawable(Drawable d) {
        mProgressFinishedDrawable = d;
    }

    @Override
    public void onClick(View v) {
        if (mOnMessageProgress != null) {
            mOnMessageProgress.onMessageProgressClick((MessageProgress) v);
        }
    }

    @Override
    public void withHideProgress() {
        for (int c = 0; c < getChildCount(); c++) {
            View child = getChildAt(c);
            if (child instanceof CircularProgressView) {
                child.setVisibility(INVISIBLE);
                break;
            }
        }
    }

    @Override
    public void onProgressUpdate(float currentProgress) {
        // empty
    }

    @Override
    public void onProgressUpdateEnd(float currentProgress) {
        if (currentProgress == 100) {
            for (int c = 0; c < getChildCount(); c++) {
                View child = getChildAt(c);
                if (child instanceof CircularProgressView) {
                    // if progress is 100, hide it automatically
                    // user doesn't need to hide manually
                    child.setVisibility(INVISIBLE);

                    // show finished drawable if supplied
                    if (mProgressFinishedDrawable != null) {
                        withDrawable(mProgressFinishedDrawable);
                    }
                    break;
                }
            }
        }
    }

    @Override
    public void onAnimationReset() {
        // empty
    }

    @Override
    public void onModeChanged(boolean isIndeterminate) {
        // empty
    }
}
