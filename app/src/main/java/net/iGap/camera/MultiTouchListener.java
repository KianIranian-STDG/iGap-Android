package net.iGap.camera;

import android.content.Context;
import android.graphics.Rect;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

public class MultiTouchListener implements View.OnTouchListener {
    private static final int INVALID_POINTER_ID = -1;
    private final GestureDetector gestureListener;
    boolean isRotateEnabled = true;
    boolean isTranslateEnabled = true;
    boolean isScaleEnabled = true;
    float minimumScale = 0.2f;
    float maximumScale = 10.0f;
    private int activePointerId = INVALID_POINTER_ID;
    private float prevX, prevY, prevRawX, prevRawY;
    private ScaleGestureDetector scaleGestureDetector;

    private int[] location = new int[2];
    private Rect outRect;
    private View deleteView;
    private ImageView photoEditImageView;
    private RelativeLayout parentView;

    private OnMultiTouchListener onMultiTouchListener;
    private OnGestureControl onGestureControl;
    boolean isTextPinchZoomable;
    private OnPhotoEditorListener onPhotoEditorListener;
    private CustomViewPager viewPager2;
    private boolean isMoving = false;

    public MultiTouchListener(@Nullable View deleteView, CustomViewPager viewPager2, RelativeLayout parentView,
                              ImageView photoEditImageView,
                              OnPhotoEditorListener onPhotoEditorListener, Context context) {
        isTextPinchZoomable = true;
        scaleGestureDetector = new ScaleGestureDetector(new ScaleGestureListener(this));
        gestureListener = new GestureDetector(context, new GestureListener());
        this.viewPager2 = viewPager2;
        this.deleteView = deleteView;
        this.parentView = parentView;
        this.photoEditImageView = photoEditImageView;
        this.onPhotoEditorListener = onPhotoEditorListener;
        if (deleteView != null) {
            outRect = new Rect(deleteView.getLeft(), deleteView.getTop(),
                    deleteView.getRight(), deleteView.getBottom());
        } else {
            outRect = new Rect(0, 0, 0, 0);
        }
    }

    private float adjustAngle(float degrees) {
        if (degrees > 180.0f) {
            degrees -= 360.0f;
        } else if (degrees < -180.0f) {
            degrees += 360.0f;
        }

        return degrees;
    }

    void move(View view, TransformInfo info) {
        computeRenderOffset(view, info.pivotX, info.pivotY);
        adjustTranslation(view, info.deltaX, info.deltaY);

        float scale = view.getScaleX() * info.deltaScale;
        scale = Math.max(info.minimumScale, Math.min(info.maximumScale, scale));
        view.setScaleX(scale);
        view.setScaleY(scale);

        float rotation = adjustAngle(view.getRotation() + info.deltaAngle);
        view.setRotation(rotation);
    }

    private void adjustTranslation(View view, float deltaX, float deltaY) {
        float[] deltaVector = {deltaX, deltaY};
        view.getMatrix().mapVectors(deltaVector);
        view.setTranslationX(view.getTranslationX() + deltaVector[0]);
        view.setTranslationY(view.getTranslationY() + deltaVector[1]);
    }

    private void computeRenderOffset(View view, float pivotX, float pivotY) {
        if (view.getPivotX() == pivotX && view.getPivotY() == pivotY) {
            return;
        }

        float[] prevPoint = {0.0f, 0.0f};
        view.getMatrix().mapPoints(prevPoint);

        view.setPivotX(pivotX);
        view.setPivotY(pivotY);

        float[] currPoint = {0.0f, 0.0f};
        view.getMatrix().mapPoints(currPoint);

        float offsetX = currPoint[0] - prevPoint[0];
        float offsetY = currPoint[1] - prevPoint[1];

        view.setTranslationX(view.getTranslationX() - offsetX);
        view.setTranslationY(view.getTranslationY() - offsetY);
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        scaleGestureDetector.onTouchEvent(view, event);


        if (!isTranslateEnabled) {
            return true;
        }

        int action = event.getAction();

        int x = (int) event.getRawX();
        int y = (int) event.getRawY();

        switch (action & event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                isMoving = false;
                prevX = event.getX();
                prevY = event.getY();
                prevRawX = event.getRawX();
                prevRawY = event.getRawY();
                activePointerId = event.getPointerId(0);
                view.bringToFront();
                firePhotoEditorSDKListener(view, true);
                viewPager2.setPagingEnabled(false);
                break;
            case MotionEvent.ACTION_MOVE:
                int pointerIndexMove = event.findPointerIndex(activePointerId);
                isMoving = true;
                if (pointerIndexMove != -1) {
                    float currX = event.getX(pointerIndexMove);
                    float currY = event.getY(pointerIndexMove);
                    if (!scaleGestureDetector.isInProgress()) {
                        adjustTranslation(view, currX - prevX, currY - prevY);
                    }
                }
                viewPager2.setPagingEnabled(false);
                break;
            case MotionEvent.ACTION_CANCEL:
                isMoving = false;
                activePointerId = INVALID_POINTER_ID;
                viewPager2.setPagingEnabled(true);
                break;
            case MotionEvent.ACTION_UP:
                activePointerId = INVALID_POINTER_ID;
                if (deleteView != null && isViewInBounds(deleteView, x, y)) {
                    if (onMultiTouchListener != null)
                        onMultiTouchListener.onRemoveViewListener(view);
                } else if (!isViewInBounds(photoEditImageView, x, y)) {
                    view.animate().translationY(0).translationY(0);
                }
                firePhotoEditorSDKListener(view, false);
                viewPager2.setPagingEnabled(true);
                break;
            case MotionEvent.ACTION_POINTER_UP:
                isMoving = false;
                int pointerIndexPointerUp = (action & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
                int pointerId = event.getPointerId(pointerIndexPointerUp);
                if (pointerId == activePointerId) {
                    int newPointerIndex = pointerIndexPointerUp == 0 ? 1 : 0;
                    prevX = event.getX(newPointerIndex);
                    prevY = event.getY(newPointerIndex);
                    activePointerId = event.getPointerId(newPointerIndex);
                }
                viewPager2.setPagingEnabled(true);
                break;
        }
        gestureListener.onTouchEvent(event);
        return true;
    }

    private void firePhotoEditorSDKListener(View view, boolean isStart) {
        if (view instanceof TextView) {
            if (onMultiTouchListener != null) {
                if (onPhotoEditorListener != null) {
                    if (isStart)
                        onPhotoEditorListener.onStartViewChangeListener();
                    else
                        onPhotoEditorListener.onStopViewChangeListener();
                }
            } else {
                if (onPhotoEditorListener != null) {
                    if (isStart)
                        onPhotoEditorListener.onStartViewChangeListener();
                    else
                        onPhotoEditorListener.onStopViewChangeListener();
                }
            }
        } else {
            if (onPhotoEditorListener != null) {
                if (isStart)
                    onPhotoEditorListener.onStartViewChangeListener();
                else
                    onPhotoEditorListener.onStopViewChangeListener();
            }
        }
    }

    private boolean isViewInBounds(View view, int x, int y) {
        view.getDrawingRect(outRect);
        view.getLocationOnScreen(location);
        outRect.offset(location[0], location[1]);
        return outRect.contains(x, y);
    }

    private void setOnMultiTouchListener(OnMultiTouchListener onMultiTouchListener) {
        this.onMultiTouchListener = onMultiTouchListener;
    }

    public void setOnGestureControl(OnGestureControl onGestureControl) {
        this.onGestureControl = onGestureControl;
    }

    private final class GestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            if (onGestureControl != null && !isMoving) {
                onGestureControl.onClick();
            }
            return true;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            if (onGestureControl != null) {
                onGestureControl.onDown();
            }
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            super.onLongPress(e);
            if (onGestureControl != null) {
                onGestureControl.onLongClick();
            }
        }
    }


    public interface OnMultiTouchListener {
        void onRemoveViewListener(View removedView);
    }


}
