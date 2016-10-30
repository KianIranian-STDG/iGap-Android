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
import android.content.SharedPreferences;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class EmojiRecentsManager extends ArrayList<String> {

    private static final String PREFERENCE_NAME = "emoji";
    private static final String PREF_RECENTS = "recent_emoji";
    private static final String PREF_PAGE = "recent_page";

    private static final Object LOCK = new Object();
    private static EmojiRecentsManager sInstance;

    private final Context mContext;

    private EmojiRecentsManager(Context context) {
        mContext = context;
        loadRecents();
    }

    public static EmojiRecentsManager getInstance(Context context) {
        if (sInstance == null) {
            synchronized (LOCK) {
                if (sInstance == null) {
                    sInstance = new EmojiRecentsManager(context);
                }
            }
        }
        return sInstance;
    }

    private void loadRecents() {
        SharedPreferences prefs = getPreferences();
        String str = prefs.getString(PREF_RECENTS, "");
        StringTokenizer tokenizer = new StringTokenizer(str, "~");
        while (tokenizer.hasMoreTokens()) {
            try {
                add(tokenizer.nextToken());
            } catch (NumberFormatException e) {
                // ignored
            }
        }
    }

    private SharedPreferences getPreferences() {
        return mContext.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
    }

    public void setRecentPage(int page) {
        getPreferences().edit().putInt(PREF_PAGE, page).commit();
    }

    public void push(String object) {
        // FIXME totally inefficient way of adding the emoji to the adapter
        // TODO this should be probably replaced by a deque
        if (contains(object)) {
            super.remove(object);
        }
        add(0, object);
    }

    public void saveRecents() {
        StringBuilder str = new StringBuilder();
        int c = size();
        for (int i = 0; i < c; i++) {
            String e = get(i);
            str.append(e);
            if (i < (c - 1)) {
                str.append('~');
            }
        }
        SharedPreferences prefs = getPreferences();
        prefs.edit().putString(PREF_RECENTS, str.toString()).apply();
    }
}
