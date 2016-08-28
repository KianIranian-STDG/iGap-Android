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
import android.view.View;
import android.widget.GridView;

import com.iGap.R;
import com.iGap.adapter.EmojiAdapter;
import com.iGap.interface_package.IEmojiClickListener;
import com.iGap.interface_package.IRecents;

public class EmojiRecentsGridView extends EmojiGridView implements IRecents {
    private final EmojiAdapter mAdapter;

    public EmojiRecentsGridView(Context context, String[] emoji,
                                IRecents recents, EmojiPopup emojiPopup) {
        super(context, emoji, recents, emojiPopup);

        EmojiRecentsManager recentsManager = EmojiRecentsManager
                .getInstance(context);
        mAdapter = new EmojiAdapter(context, recentsManager);
        mAdapter.setEmojiClickListener(new IEmojiClickListener() {
            @Override
            public void onEmojiClick(View view, String emoji) {
                if (getEmojiPopup().getEmojiClickListener() != null) {
                    getEmojiPopup().getEmojiClickListener().onEmojiClick(view, emoji);
                }
            }
        });
        GridView gridView = (GridView) rootView.findViewById(R.id.emojiGridView);
        gridView.setAdapter(mAdapter);
    }

    @Override
    public void addRecent(Context context, String emoji) {
        EmojiRecentsManager recents = EmojiRecentsManager
                .getInstance(context);
        recents.push(emoji);

        // notify dataset changed
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }
}
