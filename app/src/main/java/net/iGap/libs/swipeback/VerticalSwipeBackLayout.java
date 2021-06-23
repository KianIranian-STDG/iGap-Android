
package net.iGap.libs.swipeback;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MotionEventCompat;
import androidx.core.view.ViewCompat;
import androidx.customview.widget.ViewDragHelper;
import androidx.fragment.app.Fragment;

public class VerticalSwipeBackLayout extends FrameLayout {
    private static final String TAG = "DraggableView";
    private ViewDragHelper viewDragHelper;
    private Fragment fragment;
    private View contentView;
    private final float VIEW_RELEASE_THRESHOLD = 0.3f;
    float dragPercent;

    public VerticalSwipeBackLayout(@NonNull Context context) {
        super(context);
        init();
    }

    public VerticalSwipeBackLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public VerticalSwipeBackLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        viewDragHelper = ViewDragHelper.create(this, new DragHelper());
        viewDragHelper.setMinVelocity(4000);
    }

    public View setFragment(Fragment fragment, View view) {
        addView(view);
        this.fragment = fragment;
        contentView = view;
        return this;
    }

    @Override
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        boolean isDrawChild = child == contentView;
        boolean drawChild;

        try {
            drawChild = super.drawChild(canvas, child, drawingTime);
        } catch (StackOverflowError e) {
            e.printStackTrace();
            return false;
        }
        if (isDrawChild && viewDragHelper.getViewDragState() != ViewDragHelper.STATE_IDLE) {

        }

        return drawChild;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        final int action = MotionEventCompat.getActionMasked(ev);
        if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
            viewDragHelper.cancel();
            return false;
        }
        return viewDragHelper.shouldInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        viewDragHelper.processTouchEvent(event);
        return true;
    }

    @Override
    public void computeScroll() {
        if (viewDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    class DragHelper extends ViewDragHelper.Callback {
        @Override
        public boolean tryCaptureView(@NonNull View child, int pointerId) {
            return child == contentView;
        }

        @Override
        public int clampViewPositionVertical(@NonNull View child, int top, int dy) {
            int topBound = getPaddingTop();
            int bottomBound = Math.min(child.getHeight(), Math.max(top, 0));
            Log.e(TAG, "top: " + top + " child height: " + child.getHeight() + " dy: " + dy);
            int newTop = Math.min(Math.max(top, topBound), bottomBound);

            return newTop;
        }

        @Override
        public void onViewPositionChanged(@NonNull View changedView, int left, int top, int dx, int dy) {

            dragPercent = (float) top / contentView.getHeight();
//            contentView.setAlpha(1 - dragPercent * 0.6f);
            if (dragPercent <= 0.5){
                float newDrag = dragPercent < 0.9 ? dragPercent * 2 : dragPercent;
                contentView.setBackgroundColor(Color.parseColor("#" + ConvertDecToHex.decToHex((int) (255 * (1 - newDrag))) + "000000"));
            }
            invalidate();
            if (dragPercent > 0.8) {
                if (fragment != null) {
                    fragment.getFragmentManager().popBackStackImmediate();
                }
            }
        }

        @Override
        public int getViewVerticalDragRange(@NonNull View child) {
            if (fragment != null) {
                return 1;
            }
            return 0;
        }

        @Override
        public void onViewReleased(@NonNull View releasedChild, float xvel, float yvel) {
            int top = 0;

            top = yvel > 0 || yvel == 0 && dragPercent > VIEW_RELEASE_THRESHOLD ? (releasedChild.getHeight() + 20) : 0;

            viewDragHelper.settleCapturedViewAt(0, top);
            invalidate();
        }
    }

    private static class ConvertDecToHex {
        private static final int sizeOfIntInHalfBytes = 2;
        private static final int numberOfBitsInAHalfByte = 4;
        private static final int halfByte = 0x0F;
        private static final char[] hexDigits = {
                '0', '1', '2', '3', '4', '5', '6', '7',
                '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'
        };

        public static String decToHex(int dec) {
            StringBuilder hexBuilder = new StringBuilder(sizeOfIntInHalfBytes);
            hexBuilder.setLength(sizeOfIntInHalfBytes);
            for (int i = sizeOfIntInHalfBytes - 1; i >= 0; --i) {
                int j = dec & halfByte;
                hexBuilder.setCharAt(i, hexDigits[j]);
                dec >>= numberOfBitsInAHalfByte;
            }
            return hexBuilder.toString();
        }
    }
}

