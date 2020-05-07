package net.iGap.module.customView;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Vibrator;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.Keep;
import androidx.appcompat.widget.AppCompatImageView;

import net.iGap.R;
import net.iGap.helper.LayoutCreator;

public class CallRippleView extends FrameLayout {

    private AnimatorSet animatorSet;
    private RecourseView resourceView;
    private ImageView resourceImageView;

    private Delegate delegate;
    boolean exited = false;
    boolean vibratePerformed;

    public CallRippleView(Context context) {
        super(context);
        int startRadius = LayoutCreator.dp(42);
        int endRadius = LayoutCreator.dp(100);
        int strokeWidth = LayoutCreator.dp(2);

        Paint paint = new Paint();
        paint.setColor(getResources().getColor(R.color.white));
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(0);

        resourceView = new RecourseView(getContext(), paint, startRadius);
        addView(resourceView, LayoutCreator.createFrame(2 * (endRadius + strokeWidth), 2 * (endRadius + strokeWidth), Gravity.CENTER));

        resourceImageView = new AppCompatImageView(getContext());
        addView(resourceImageView, LayoutCreator.createFrame(64, 64, Gravity.CENTER));

        animatorSet = new AnimatorSet();
        ObjectAnimator radius = ObjectAnimator.ofFloat(resourceView, "radius", LayoutCreator.dp(30), LayoutCreator.dp(55));
        ObjectAnimator alpha = ObjectAnimator.ofFloat(resourceView, View.ALPHA, 1f, 0f);

        radius.setRepeatCount(ValueAnimator.INFINITE);
        alpha.setRepeatCount(ValueAnimator.INFINITE);

        animatorSet.setDuration(2200);
        animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animatorSet.playTogether(radius, alpha);
    }

    public void setDelegate(Delegate delegate) {
        this.delegate = delegate;
    }

    public void setImageResource(int resId) {
        resourceImageView.setImageResource(resId);
    }

    public void shakeView(float x, int num) {
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(ObjectAnimator.ofFloat(resourceImageView, View.TRANSLATION_X, LayoutCreator.dp(x)));
        animatorSet.setDuration(50);
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                shakeView(num == 5 ? 0 : -x, num + 1);
            }
        });
        animatorSet.start();
    }

    public void startAnimation() {
        if (animatorSet.isRunning())
            return;

        animatorSet.start();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            stopAnimation();
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {

            if (exited && !vibratePerformed) {
                vibrate();
                vibratePerformed = true;
                exited = false;
                return true;
            }

            if (x > getMeasuredWidth() || x < 0 || y < 0 || y > getMeasuredHeight()) {
                exited = true;
                return true;
            } else if (x < getMeasuredWidth() || y < getMeasuredHeight()) {
                int rad;

                rad = x / 2;

                vibratePerformed = false;

                resourceView.setAlpha(80);
                resourceView.setRadius(rad);
                return false;
            }
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            exited = false;
            if (vibratePerformed) {
                if (delegate != null)
                    delegate.onCallAnswered();
            } else {
                startAnimation();
            }
        }

        return true;
    }

    private void stopAnimation() {
        if (!animatorSet.isRunning())
            return;

        animatorSet.end();
    }

    private void vibrate() {
        Vibrator vibrator = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null)
            vibrator.vibrate(100);
    }

    private static class RecourseView extends View {
        private Paint viewPaint;
        private float viewRadius;

        public float getRadius() {
            return viewRadius;
        }

        public void setAlpha(int alpha) {
            viewPaint.setAlpha(alpha);
        }

        @Keep
        public void setRadius(float radius) {
            viewRadius = radius;
            invalidate();
        }

        public RecourseView(Context context, Paint paint, float radius) {
            super(context);
            viewPaint = paint;
            viewRadius = radius;
        }

        @Override
        protected void onDraw(Canvas canvas) {
            int centerX = getWidth() / 2;
            int centerY = getHeight() / 2;
            canvas.drawCircle(centerX, centerY, viewRadius, viewPaint);
        }
    }

    @FunctionalInterface
    public interface Delegate {
        void onCallAnswered();
    }
}
