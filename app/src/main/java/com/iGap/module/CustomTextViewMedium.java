package com.iGap.module;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class CustomTextViewMedium extends TextView {
    public static Typeface FONT_NAME;


    public CustomTextViewMedium(Context context) {
        super(context);
        if(FONT_NAME == null) FONT_NAME = Typeface.createFromAsset(context.getAssets(), "fonts/IRANSansMedium.ttf");
        this.setTypeface(FONT_NAME);
    }
    public CustomTextViewMedium(Context context, AttributeSet attrs) {
        super(context, attrs);
        if(FONT_NAME == null) FONT_NAME = Typeface.createFromAsset(context.getAssets(), "fonts/IRANSansMedium.ttf");
        this.setTypeface(FONT_NAME);
    }
    public CustomTextViewMedium(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        if(FONT_NAME == null) FONT_NAME = Typeface.createFromAsset(context.getAssets(), "fonts/IRANSansMedium.ttf");
        this.setTypeface(FONT_NAME);
    }
}