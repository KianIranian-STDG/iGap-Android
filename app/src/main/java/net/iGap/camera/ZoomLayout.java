package net.iGap.camera;

import android.content.Context;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import net.iGap.libs.emojiKeyboard.NotifyFrameLayout;
import net.iGap.module.AndroidUtils;

public class ZoomLayout extends FrameLayout implements ScaleGestureDetector.OnScaleGestureListener {

    private int keyboardHeight;
    private NotifyFrameLayout.Listener listener;
    private Rect rect = new Rect();

    private enum Mode {
        NONE,
        DRAG,
        ZOOM
    }

    private static final float MIN_ZOOM = 1.0f;
    private static final float MAX_ZOOM = 4.0f;
    private static final int CLICK_TIME_DIFFERENCE_THRESHOLD = 300;

    private Mode mode = Mode.NONE;
    private float scale = 1.0f;
    private float lastScaleFactor = 0f;

    // Where the finger first  touches the screen
    private float startX = 0f;
    private float startY = 0f;

    // How much to translate the canvas
    private float dx = 0f;
    private float dy = 0f;
    private float prevDx = 0f;
    private float prevDy = 0f;

    // Custom vars to handle double tap
    private boolean firstTouch = false;
    private long time = System.currentTimeMillis();
    private boolean restore = false;

    public void setListener(NotifyFrameLayout.Listener listener) {
        this.listener = listener;
    }

    public interface Listener {
        void onSizeChanged(int keyboardSize, boolean land);
    }

    public ZoomLayout(@NonNull Context context) {
        super(context);
        init(context);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        notifyHeightChanged();
    }
    private void init(Context context) {
        final ScaleGestureDetector scaleDetector = new ScaleGestureDetector(context, this);
        setOnTouchListener((view, motionEvent) -> {

            switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                    if (firstTouch && (System.currentTimeMillis() - time) <= CLICK_TIME_DIFFERENCE_THRESHOLD) {
                        //do stuff here for double tap
                        if (restore) {
                            scale = 1.0f;
                            restore = false;
                        } else {
                            scale *= 2.0f;
                            restore = true;
                        }
                        mode = Mode.ZOOM;
                        firstTouch = false;

                    } else {
                        if (scale > MIN_ZOOM) {
                            mode = Mode.DRAG;
                            startX = motionEvent.getX() - prevDx;
                            startY = motionEvent.getY() - prevDy;
                        }
                        firstTouch = true;
                        time = System.currentTimeMillis();
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (mode == Mode.DRAG) {
                        dx = motionEvent.getX() - startX;
                        dy = motionEvent.getY() - startY;
                    }
                    break;
                case MotionEvent.ACTION_POINTER_DOWN:
                    mode = Mode.ZOOM;
                    break;
                case MotionEvent.ACTION_POINTER_UP:
                    mode = Mode.NONE;
                    break;
                case MotionEvent.ACTION_UP:
                    mode = Mode.NONE;
                    prevDx = dx;
                    prevDy = dy;
                    break;
            }
            scaleDetector.onTouchEvent(motionEvent);

            if ((mode == Mode.DRAG && scale >= MIN_ZOOM) || mode == Mode.ZOOM) {
                getParent().requestDisallowInterceptTouchEvent(true);
                float maxDx = (child().getWidth() - (child().getWidth() / scale)) / 2 * scale;
                float maxDy = (child().getHeight() - (child().getHeight() / scale)) / 2 * scale;
                dx = Math.min(Math.max(dx, -maxDx), maxDx);
                dy = Math.min(Math.max(dy, -maxDy), maxDy);
                applyScaleAndTranslation();
            }

            return true;
        });
    }
    public void setChildScale(float scaleFactor) {
        scale = scaleFactor;
        child().setScaleX(scaleFactor);
        child().setScaleY(scaleFactor);
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector scaleDetector) {
        return true;
    }

    @Override
    public boolean onScale(ScaleGestureDetector scaleDetector) {
        float scaleFactor = scaleDetector.getScaleFactor();
        if (lastScaleFactor == 0 || (Math.signum(scaleFactor) == Math.signum(lastScaleFactor))) {
            scale *= scaleFactor;
            scale = Math.max(MIN_ZOOM, Math.min(scale, MAX_ZOOM));
            lastScaleFactor = scaleFactor;
        } else {
            lastScaleFactor = 0;
        }
        return true;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector scaleDetector) {
    }

    private void applyScaleAndTranslation() {
        child().setScaleX(scale);
        child().setScaleY(scale);
        child().setTranslationX(dx);
        child().setTranslationY(dy);
    }

    private View child() {
        return getChildAt(0);
    }
    public int getKeyboardHeight() {
        View rootView = getRootView();
        getWindowVisibleDisplayFrame(rect);
        if (rect.bottom == 0 && rect.top == 0) {
            return 0;
        }
        int getViewInset = AndroidUtils.getViewInset(rootView);
        int usableViewHeight = rootView.getHeight() - (rect.top != 0 ? AndroidUtils.statusBarHeight : 0) - getViewInset;
        return Math.max(0, usableViewHeight - (rect.bottom - rect.top));
    }

    public void notifyHeightChanged() {
        if (listener != null) {
            keyboardHeight = getKeyboardHeight();
            final boolean land = AndroidUtils.displaySize.x > AndroidUtils.displaySize.y;
            post(() -> {
                if (listener != null) {
                    listener.onSizeChanged(keyboardHeight, land);
                }
            });
        }
    }

}
