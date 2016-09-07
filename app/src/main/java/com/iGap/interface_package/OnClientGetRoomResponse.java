package com.iGap.interface_package;

import com.iGap.proto.ProtoClientGetRoom;
import com.iGap.proto.ProtoGlobal;

/**
 * Created by Alireza Eskandarpour Shoferi (meNESS) on 9/7/2016.
 */
public interface OnClientGetRoomResponse {
    void onClientGetRoomResponse(ProtoGlobal.Room room, ProtoClientGetRoom.ClientGetRoomResponse.Builder builder);
}
