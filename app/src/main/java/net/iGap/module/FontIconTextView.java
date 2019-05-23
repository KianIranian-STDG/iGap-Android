/*
* This is the source code of iGap for Android
* It is licensed under GNU AGPL v3.0
* You should have received a copy of the license in this archive (see LICENSE).
* Copyright © 2017 , iGap - www.iGap.net
* iGap Messenger | Free, Fast and Secure instant messaging application
* The idea of the Kianiranian Company - www.kianiranian.com
* All rights reserved.
*/

package net.iGap.module;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.view.Gravity;

import net.iGap.G;
import net.iGap.R;
import net.iGap.helper.Emojione;

import java.lang.reflect.Type;

public class FontIconTextView extends AppCompatTextView {

    public FontIconTextView(Context context) {
        super(context);
        init(context);
    }

    public FontIconTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public FontIconTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {

        Typeface tf = Typeface.createFromAsset(G.context.getAssets(), "fonts/font_icon.ttf");
        setTypeface(tf);
        setGravity(Gravity.CENTER);

    }

}
