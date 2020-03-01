package net.iGap.module.customView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.appcompat.widget.AppCompatEditText;

public class EventEditText extends AppCompatEditText {

    private Listener listener;

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public interface Listener {
        void onInternalTouchEvent(MotionEvent event);
    }

    public EventEditText(Context context) {
        super(context);
    }

    public EventEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (listener != null)
            listener.onInternalTouchEvent(event);

        try {
            return super.onTouchEvent(event);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
