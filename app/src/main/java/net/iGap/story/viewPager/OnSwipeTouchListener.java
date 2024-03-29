package net.iGap.story.viewPager;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public class OnSwipeTouchListener implements View.OnTouchListener {

    private GestureDetector gestureDetector;
    private float startX = 0f;
    private float startY = 0f;
    private long touchDownTime = 0L;
    private static final int SWIPE_THRESHOLD = 100;
    private static final int SWIPE_VELOCITY_THRESHOLD = 100;
    private static final long CLICK_TIME_THRESHOLD = 400L;
    private static final long TOUCH_MOVE_THRESHOLD = 300L;

    public OnSwipeTouchListener(Context context) {
        gestureDetector = new GestureDetector(context, new GestureListener());
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        onTouchView(view, event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touchDownTime = now();
                startX = event.getX();
                startY = event.getY();
                break;
            case MotionEvent.ACTION_UP:
                float endX = event.getX();
                float endY = event.getY();
                if (isAClick(startX, endX, startY, endY)) {
                    onClick(view);
                }
                break;
        }
        return gestureDetector.onTouchEvent(event);
    }

    private boolean isAClick(float startX, float endX, float startY, float endY) {
        boolean isTouchDuration = now() - touchDownTime < CLICK_TIME_THRESHOLD;  // short time mean this is a click
        boolean isTouchLength = Math.abs(endX - startX) + Math.abs(endY - startY) < TOUCH_MOVE_THRESHOLD; // short length mean this is a click
        return isTouchDuration && isTouchLength;
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            boolean result = false;
            try {
                float diffY = e2.getY() - e1.getY();
                if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffY > 0) {
                        onSwipeBottom();
                    } else {
                        onSwipeTop();
                    }
                    result = true;
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
            return result;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            onLongClick();
        }
    }

    private long now() {
        return System.currentTimeMillis();
    }

    public void onClick(View view) {
    }

    public void onLongClick() {
    }

    public boolean onTouchView(View view, MotionEvent event) {
        return false;
    }

    public void onSwipeTop() {
    }

    public void onSwipeBottom() {
    }
}
