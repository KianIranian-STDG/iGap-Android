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

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.iGap.R;
import com.iGap.interfaces.IEmojiClickListener;
import com.iGap.interfaces.IEmojiLongClickListener;
import java.util.List;

public class EmojiAdapter extends ArrayAdapter<String> {
    private IEmojiClickListener mEmojiClickListener;
    private IEmojiLongClickListener mEmojiLongClickListener;

    public EmojiAdapter(Context context, List<String> data) {
        super(context, R.layout.item_emoji, data);
    }

    public EmojiAdapter(Context context, String[] data) {
        super(context, R.layout.item_emoji, data);
    }

    public void setEmojiLongClickListener(IEmojiLongClickListener listener) {
        this.mEmojiLongClickListener = listener;
    }

    public void setEmojiClickListener(IEmojiClickListener listener) {
        this.mEmojiClickListener = listener;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            v = LayoutInflater.from(getContext()).inflate(R.layout.item_emoji, null, false);
            ViewHolder holder = new ViewHolder();
            holder.emoji = (TextView) v.findViewById(R.id.emoji);
            v.setTag(holder);
        }

        String emoji = getItem(position);
        ViewHolder holder = (ViewHolder) v.getTag();
        holder.emoji.setText(emoji);
        holder.emoji.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                return mEmojiLongClickListener != null && mEmojiLongClickListener.onEmojiLongClick(view, getItem(position));
            }
        });
        holder.emoji.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mEmojiClickListener != null) {
                    mEmojiClickListener.onEmojiClick(v, getItem(position));
                }
            }
        });
        return v;
    }

    class ViewHolder {
        TextView emoji;
    }
}