package com.iGap.interface_package;

import android.view.View;

import com.iGap.module.StructMessageInfo;

/**
 * Created by Alireza Eskandarpour Shoferi (meNESS) on 9/4/2016.
 */

/**
 * when chat messages has clicked
 */
public interface OnMessageClick {
    void onMessageClick(View view, StructMessageInfo messageInfo, int position);
}
