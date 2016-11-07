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

package com.iGap.adapter;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.iGap.module.EmojiGridView;
import com.iGap.module.EmojiRecentsGridView;

import java.util.List;

public class EmojiPagerAdapter extends PagerAdapter {
    private final List<EmojiGridView> mViews;

    public EmojiPagerAdapter(List<EmojiGridView> views) {
        super();
        this.mViews = views;
    }

    public EmojiRecentsGridView getRecentFragment() {
        for (EmojiGridView it : mViews) {
            if (it instanceof EmojiRecentsGridView) return (EmojiRecentsGridView) it;
        }
        return null;
    }

    @Override
    public int getCount() {
        return mViews.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View v = mViews.get(position).rootView;
        container.addView(v, 0);
        return v;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object view) {
        container.removeView((View) view);
    }

    @Override
    public boolean isViewFromObject(View view, Object key) {
        return key == view;
    }
}
