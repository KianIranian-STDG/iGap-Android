package com.iGap.interfaces;

import com.iGap.proto.ProtoGlobal;

import java.util.List;

/**
 * Created by Alireza Eskandarpour Shoferi (meNESS) on 9/5/2016.
 */
public interface OnClientGetRoomHistoryResponse {
    void onGetRoomHistory(long roomId, List<ProtoGlobal.RoomMessage> messages);

    void onGetRoomHistoryError(int majorCode, int minorCode);
}
