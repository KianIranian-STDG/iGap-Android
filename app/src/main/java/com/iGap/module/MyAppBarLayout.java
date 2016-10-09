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
import android.support.design.widget.AppBarLayout;
import android.util.AttributeSet;

public class MyAppBarLayout extends AppBarLayout implements AppBarLayout.OnOffsetChangedListener {
    private OnMoveListener mListener;

    public MyAppBarLayout(Context context, OnMoveListener listener) {
        super(context);

        mListener = listener;

        init();
    }

    private void init() {
        addOnOffsetChangedListener(this);
    }

    public MyAppBarLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public void addOnMoveListener(OnMoveListener listener) {
        this.mListener = listener;
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        int verticalOffsetAbs = Math.abs(verticalOffset);
        int appBarLayoutHeight = appBarLayout.getHeight();


        if (verticalOffset == 0) {
            if (mListener != null) {
                mListener.onAppBarLayoutMove(appBarLayout, verticalOffset, false);
            }
            return;
        } else if (verticalOffset == appBarLayoutHeight) {
            if (mListener != null) {
                mListener.onAppBarLayoutMove(appBarLayout, verticalOffset, true);
            }
            return;
        }


        if (mListener != null) {
            // FIXME: 9/24/2016 [Alireza Eskandarpour Shoferi] bad tashkhis mide ke be bala scroll mishe ya paeen
            mListener.onAppBarLayoutMove(appBarLayout, verticalOffset, verticalOffsetAbs > appBarLayoutHeight - Utils.getStatusBarHeight(getContext()));
        }
    }

    public interface OnMoveListener {
        void onAppBarLayoutMove(AppBarLayout appBarLayout, int verticalOffset, boolean moveUp);
    }
}
