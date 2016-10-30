package com.iGap.interfaces;

import com.iGap.proto.ProtoGlobal;

/**
 * Created by Alireza Eskandarpour Shoferi (meNESS) on 9/5/2016.
 */
public interface OnChatSendMessageResponse {
    // message updated after send request sent
    void onMessageUpdate(long roomId, long messageId, ProtoGlobal.RoomMessageStatus status,
        String identity, ProtoGlobal.RoomMessage roomMessage);

    void onMessageReceive(long roomId, String message, String messageType,
        ProtoGlobal.RoomMessage roomMessage, ProtoGlobal.Room.Type roomType);
}
