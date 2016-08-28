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

package com.iGap.interface_package;

import android.view.View;

import com.iGap.module.EmojiPopup;

/**
 * Created by Alireza Eskandarpour Shoferi on 8/26/2016.
 */
public interface IEmojiViewCreate {
    void onEmojiViewCreate(View view, EmojiPopup emojiPopup);
}
