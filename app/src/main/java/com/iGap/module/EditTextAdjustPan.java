package com.iGap.module;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.EditText;

public class EditTextAdjustPan extends EditText {
    public EditTextAdjustPan(Context context) {
        super(context);
    }

    public EditTextAdjustPan(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EditTextAdjustPan(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            clearFocus();
        }
        return super.onKeyPreIme(keyCode, event);
    }
}


