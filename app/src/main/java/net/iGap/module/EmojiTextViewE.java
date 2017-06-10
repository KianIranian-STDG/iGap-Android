package net.iGap.module;

import android.content.Context;
import android.util.AttributeSet;
import com.vanniktech.emoji.EmojiTextView;


public class EmojiTextViewE extends EmojiTextView {

    public boolean hasEmoji;

    public EmojiTextViewE(Context context) {
        super(context);
    }

    public EmojiTextViewE(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
}
