package com.iGap.interfaces;

import com.iGap.proto.ProtoGlobal;

/**
 * Created by Alireza Eskandarpour Shoferi (meNESS) on 9/5/2016.
 */
public interface OnClientGetRoomHistoryResponse {
    void onGetRoomHistory(long roomId, String message, String messageType,
        ProtoGlobal.RoomMessage roomMessage);

    void onGetRoomHistoryError(int majorCode, int minorCode);
}
