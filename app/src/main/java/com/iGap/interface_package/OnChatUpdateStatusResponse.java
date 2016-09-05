package com.iGap.interface_package;

import com.iGap.proto.ProtoGlobal;

/**
 * Created by Alireza Eskandarpour Shoferi (meNESS) on 9/5/2016.
 */
public interface OnChatUpdateStatusResponse {
    void onChatUpdateStatus(long roomId, long messageId, ProtoGlobal.RoomMessageStatus status, int statusVersion);
}
