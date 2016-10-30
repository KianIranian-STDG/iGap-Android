package com.iGap.module;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;
import com.iGap.helper.FontCache;

public class CustomTextViewMedium extends TextView {
    public CustomTextViewMedium(Context context) {
        super(context);
        init(context);
    }

    public CustomTextViewMedium(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CustomTextViewMedium(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        setTypeface(FontCache.get("fonts/IRANSansMedium.ttf", context));
    }
}