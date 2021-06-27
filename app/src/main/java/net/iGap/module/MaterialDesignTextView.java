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
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.res.ResourcesCompat;

import net.iGap.R;

public class MaterialDesignTextView extends AppCompatTextView {

    public MaterialDesignTextView(Context context) {
        super(context);
        init();
    }

    public MaterialDesignTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MaterialDesignTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        setTypeface(ResourcesCompat.getFont(getContext(), R.font.font_icons));
        setText(getText());
    }
}
