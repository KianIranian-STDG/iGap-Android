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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridView;

import com.iGap.R;
import com.iGap.adapter.EmojiAdapter;
import com.iGap.emoji.People;
import com.iGap.interface_package.IEmojiClickListener;
import com.iGap.interface_package.IEmojiLongClickListener;
import com.iGap.interface_package.IRecents;

public class EmojiGridView {
    public final View rootView;
    private final EmojiPopup mEmojiPopup;
    private IRecents mRecents;

    public EmojiGridView(final Context context, String[] emojis, IRecents recents, EmojiPopup emojiPopup) {
        mEmojiPopup = emojiPopup;
        rootView = LayoutInflater.from(context).inflate(R.layout.emoji_grid_view, null);
        setRecents(recents);
        GridView gridView = (GridView) rootView.findViewById(R.id.emojiGridView);
        String[] data;
        if (emojis == null) {
            data = People.DATA;
        } else {
            data = emojis;
        }
        EmojiAdapter mAdapter = new EmojiAdapter(context, data);
        mAdapter.setEmojiLongClickListener(new IEmojiLongClickListener() {
            @Override
            public boolean onEmojiLongClick(View view, String emoji) {
                return mEmojiPopup.getEmojiLongClickListener() != null && mEmojiPopup.getEmojiLongClickListener().onEmojiLongClick(view, emoji);
            }
        });
        mAdapter.setEmojiClickListener(new IEmojiClickListener() {

            @Override
            public void onEmojiClick(View view, String emoji) {
                if (mEmojiPopup.getEmojiClickListener() != null) {
                    mEmojiPopup.getEmojiClickListener().onEmojiClick(view, emoji);
                }
                if (mRecents != null) {
                    mRecents.addRecent(context, emoji);
                }
            }
        });
        gridView.setAdapter(mAdapter);
    }

    private void setRecents(IRecents recents) {
        mRecents = recents;
    }

    public EmojiPopup getEmojiPopup() {
        return mEmojiPopup;
    }

}
