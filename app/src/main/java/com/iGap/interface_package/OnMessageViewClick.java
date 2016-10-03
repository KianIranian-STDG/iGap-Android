package com.iGap.interface_package;

import android.view.View;

import com.iGap.module.StructMessageInfo;
import com.iGap.proto.ProtoGlobal;

/**
 * Created by Alireza Eskandarpour Shoferi (meNESS) on 9/4/2016.
 */

/**
 * when chat messages has clicked
 */
public interface OnMessageViewClick {
    void onMessageFileClick(View view, StructMessageInfo messageInfo, int position, ProtoGlobal.RoomMessageType fileType);

    void onSenderAvatarClick(View view, StructMessageInfo messageInfo, int position);
}
