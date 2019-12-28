package net.iGap.module;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;

import com.vanniktech.emoji.EmojiEditText;


public class EmojiEditTextE extends EmojiEditText {

    private Listener listener;

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public interface Listener {
        void onInternalTouchEvent(MotionEvent event);
    }

    public EmojiEditTextE(Context context) {
        super(context);
    }

    public EmojiEditTextE(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
        outAttrs.imeOptions = EditorInfo.IME_FLAG_NO_EXTRACT_UI;
        return super.onCreateInputConnection(outAttrs);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (listener != null)
            listener.onInternalTouchEvent(event);

        return super.onTouchEvent(event);
    }
}
