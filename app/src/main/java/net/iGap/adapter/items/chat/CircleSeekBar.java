package net.iGap.adapter.items.chat;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;

import net.iGap.R;

public class CircleSeekBar extends View {

    private static int INVALID_PROGRESS_VALUE = -1;
    private Drawable drawable;
    private int max = 100;
    private int progress = 0;
    private int progressWidth = 4;
    private int arcWidth = 2;
    private int startAngle = 0;
    private int sweepAngle = 360;
    private int rotation = 0;
    private boolean roundedEdges = false;
    private boolean touchInside = true;
    private boolean clockwise = true;
    private boolean enabled = true;
    private int arcRadius = 0;
    private float progressSweep = 0;
    private RectF arcRect = new RectF();
    private Paint arcPaint;
    private Paint progressPaint;
    private int translateX;
    private int translateY;
    private int thumbXPos;
    private int thumbYPos;
    private float touchIgnoreRadius;
    private OnSeekArcChangeListener callBack;

    public CircleSeekBar(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        float density = context.getResources().getDisplayMetrics().density;
        int thumbHeight;
        int thumbWidth;
        drawable = getContext().getResources().getDrawable(R.drawable.shape_circle_progress);
        progressWidth = (int) (progressWidth * density);
        thumbHeight = drawable.getIntrinsicHeight() / 2;
        thumbWidth = drawable.getIntrinsicWidth() / 2;
        drawable.setBounds(-thumbWidth, -thumbHeight, thumbWidth, thumbHeight);

        progress = (progress > max) ? max : progress;
        progress = (progress < 0) ? 0 : progress;

        sweepAngle = (sweepAngle > 360) ? 360 : sweepAngle;
        sweepAngle = (sweepAngle < 0) ? 0 : sweepAngle;

        startAngle = (startAngle > 360) ? 0 : startAngle;
        startAngle = (startAngle < 0) ? 0 : startAngle;

        arcPaint = new Paint();
        progressPaint = new Paint();

        progressSweep = (float) progress / max * sweepAngle;

        arcPaint.setColor(getContext().getResources().getColor(R.color.voice_round));
        progressPaint.setColor(getContext().getResources().getColor(R.color.voice_round));

        arcPaint.setAntiAlias(true);
        arcPaint.setStyle(Paint.Style.STROKE);
        arcPaint.setStrokeWidth(arcWidth);

        progressPaint.setAntiAlias(true);
        progressPaint.setStyle(Paint.Style.STROKE);
        progressPaint.setStrokeWidth(progressWidth);

        if (roundedEdges) {
            arcPaint.setStrokeCap(Paint.Cap.ROUND);
            progressPaint.setStrokeCap(Paint.Cap.ROUND);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (!clockwise) {
            canvas.scale(-1, 1, arcRect.centerX(), arcRect.centerY());
        }
        int angleOffset = -90;
        final int arcStart = startAngle + angleOffset + rotation;
        final int arcSweep = sweepAngle;
        canvas.drawArc(arcRect, arcStart, arcSweep, false, arcPaint);
        canvas.drawArc(arcRect, arcStart, progressSweep, false, progressPaint);
        if (enabled) {
            canvas.translate(translateX - thumbXPos, translateY - thumbYPos);
            drawable.draw(canvas);
        }
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        final int height = getDefaultSize(getSuggestedMinimumHeight(),
                heightMeasureSpec);
        final int width = getDefaultSize(getSuggestedMinimumWidth(),
                widthMeasureSpec);
        final int min = Math.min(width, height);
        float top = 0;
        float left = 0;
        int arcDiameter = 0;
        translateX = (int) (width * 0.5f);
        translateY = (int) (height * 0.5f);
        arcDiameter = min - getPaddingLeft();
        arcRadius = arcDiameter / 2;
        top = height / 2 - (arcDiameter / 2);
        left = width / 2 - (arcDiameter / 2);
        arcRect.set(left, top, left + arcDiameter, top + arcDiameter);
        int arcStart = (int) progressSweep + startAngle + rotation + 90;
        thumbXPos = (int) (arcRadius * Math.cos(Math.toRadians(arcStart)));
        thumbYPos = (int) (arcRadius * Math.sin(Math.toRadians(arcStart)));
        setTouchInSide(touchInside);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (enabled) {
            this.getParent().requestDisallowInterceptTouchEvent(true);
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    onStartTrackingTouch();
                    updateOnTouch(event);
                    break;
                case MotionEvent.ACTION_MOVE:
                    updateOnTouch(event);
                    break;
                case MotionEvent.ACTION_UP:
                    onStopTrackingTouch();
                    setPressed(false);
                    this.getParent().requestDisallowInterceptTouchEvent(false);
                    break;
                case MotionEvent.ACTION_CANCEL:
                    onStopTrackingTouch();
                    setPressed(false);
                    this.getParent().requestDisallowInterceptTouchEvent(false);
                    break;
            }
            return true;
        }
        return false;
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        if (drawable != null && drawable.isStateful()) {
            int[] state = getDrawableState();
            drawable.setState(state);
        }
        invalidate();
    }

    private void onStartTrackingTouch() {
        if (callBack != null) {
            callBack.onStartTrackingTouch(this);
        }
    }

    private void onStopTrackingTouch() {
        if (callBack != null) {
            callBack.onStopTrackingTouch(this);
        }
    }

    private void updateOnTouch(MotionEvent event) {
        boolean ignoreTouch = ignoreTouch(event.getX(), event.getY());
        if (ignoreTouch) {
            return;
        }
        setPressed(true);
        double touchAngle = getTouchDegrees(event.getX(), event.getY());
        int progress = getProgressForAngle(touchAngle);
        onProgressRefresh(progress, true);
    }

    private boolean ignoreTouch(float xPos, float yPos) {
        boolean ignore = false;
        float x = xPos - translateX;
        float y = yPos - translateY;

        float touchRadius = (float) Math.sqrt(((x * x) + (y * y)));
        if (touchRadius < touchIgnoreRadius) {
            ignore = true;
        }
        return ignore;
    }

    private double getTouchDegrees(float xPos, float yPos) {
        float x = xPos - translateX;
        float y = yPos - translateY;
        x = (clockwise) ? x : -x;
        double angle = Math.toDegrees(Math.atan2(y, x) + (Math.PI / 2)
                - Math.toRadians(rotation));
        if (angle < 0) {
            angle = 360 + angle;
        }
        angle -= startAngle;
        return angle;
    }

    private int getProgressForAngle(double angle) {
        int touchProgress = (int) Math.round(valuePerDegree() * angle);

        touchProgress = (touchProgress < 0) ? INVALID_PROGRESS_VALUE
                : touchProgress;
        touchProgress = (touchProgress > max) ? INVALID_PROGRESS_VALUE
                : touchProgress;
        return touchProgress;
    }

    private float valuePerDegree() {
        return (float) max / sweepAngle;
    }

    private void onProgressRefresh(int progress, boolean fromUser) {
        updateProgress(progress, fromUser);
    }

    private void updateThumbPosition() {
        int thumbAngle = (int) (startAngle + progressSweep + rotation + 90);
        thumbXPos = (int) (arcRadius * Math.cos(Math.toRadians(thumbAngle)));
        thumbYPos = (int) (arcRadius * Math.sin(Math.toRadians(thumbAngle)));
    }

    private void updateProgress(int progress, boolean fromUser) {
        if (progress == INVALID_PROGRESS_VALUE) {
            return;
        }
        progress = (progress > max) ? max : progress;
        progress = (progress < 0) ? 0 : progress;
        this.progress = progress;
        if (callBack != null) {
            callBack.onProgressChanged(this, progress, fromUser);
        }
        progressSweep = (float) progress / max * sweepAngle;
        updateThumbPosition();
        invalidate();
    }


    public void setOnSeekArcChangeListener(OnSeekArcChangeListener l) {
        callBack = l;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        updateProgress(progress, false);
    }

    public void setProgressWidth(int mProgressWidth) {
        this.progressWidth = mProgressWidth;
        progressPaint.setStrokeWidth(mProgressWidth);
    }

    public void setArcWidth(int mArcWidth) {
        this.arcWidth = mArcWidth;
        arcPaint.setStrokeWidth(mArcWidth);
    }

    public void setRoundedEdges(boolean isEnabled) {
        roundedEdges = isEnabled;
        if (roundedEdges) {
            arcPaint.setStrokeCap(Paint.Cap.ROUND);
            progressPaint.setStrokeCap(Paint.Cap.ROUND);
        } else {
            arcPaint.setStrokeCap(Paint.Cap.SQUARE);
            progressPaint.setStrokeCap(Paint.Cap.SQUARE);
        }
    }

    public void setTouchInSide(boolean isEnabled) {
        int thumbHalfheight = drawable.getIntrinsicHeight() / 2;
        int thumbHalfWidth = drawable.getIntrinsicWidth() / 2;
        touchInside = isEnabled;
        if (touchInside) {
            touchIgnoreRadius = (float) arcRadius / 4;
        } else {
            // Don't use the exact radius makes interaction too tricky
            touchIgnoreRadius = arcRadius
                    - Math.min(thumbHalfWidth, thumbHalfheight);
        }
    }

    public void setClockwise(boolean isClockwise) {
        clockwise = isClockwise;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setProgressColor(int color) {
        progressPaint.setColor(color);
        invalidate();
    }

    public void setArcColor(int color) {
        arcPaint.setColor(color);
        invalidate();
    }

    public int getMax() {
        return max;
    }

    public void setMax(int mMax) {
        this.max = mMax;
    }

    public interface OnSeekArcChangeListener {

        void onProgressChanged(CircleSeekBar seekArc, int progress, boolean fromUser);

        void onStartTrackingTouch(CircleSeekBar seekArc);

        void onStopTrackingTouch(CircleSeekBar seekArc);
    }
}
