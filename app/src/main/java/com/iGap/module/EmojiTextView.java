/*
 * Copyright 2016 Alireza Eskandarpour Shoferi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.iGap.module;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import com.iGap.helper.Emojione;
import com.iGap.helper.FontCache;

public class EmojiTextView extends AppCompatTextView {

    public EmojiTextView(Context context) {
        super(context);
        init(context);
    }

    public EmojiTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public EmojiTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        setTypeface(FontCache.get("fonts/emojione-android.ttf", context));

        setText(getText());
    }

    @Override public void setText(CharSequence text, BufferType type) {

        if (text.toString().contains("#")) {
            super.setText(text, type);
        } else {
            super.setText(text != null ? Emojione.shortnameToUnicode(text.toString(), false) : text,
                type);
        }
    }
}
