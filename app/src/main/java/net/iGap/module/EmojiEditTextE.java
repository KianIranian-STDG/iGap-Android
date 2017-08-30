package net.iGap.module;

import android.content.Context;
import android.util.AttributeSet;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import com.vanniktech.emoji.EmojiEditText;


public class EmojiEditTextE extends EmojiEditText {
    public EmojiEditTextE(Context context) {
        super(context);
    }

    public EmojiEditTextE(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
        InputConnection conn = super.onCreateInputConnection(outAttrs);
        outAttrs.imeOptions = EditorInfo.IME_FLAG_NO_ENTER_ACTION;
        return conn;
    }
}
