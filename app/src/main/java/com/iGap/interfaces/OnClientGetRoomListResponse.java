package com.iGap.interfaces;

import com.iGap.proto.ProtoGlobal;
import com.iGap.proto.ProtoResponse;

import java.util.List;

/**
 * Created by Alireza Eskandarpour Shoferi (meNESS) on 8/31/2016.
 */
public interface OnClientGetRoomListResponse {
    void onClientGetRoomList(List<ProtoGlobal.Room> roomList, ProtoResponse.Response response);

    void onError(int majorCode, int minorCode);
}
