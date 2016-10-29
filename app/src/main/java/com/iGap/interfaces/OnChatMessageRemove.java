package com.iGap.interfaces;

import com.iGap.module.StructMessageInfo;

/**
 * Created by Alireza Eskandarpour Shoferi (meNESS) on 9/17/2016.
 */
public interface OnChatMessageRemove {
    void onPreChatMessageRemove(StructMessageInfo messageInfo, int position);
}
