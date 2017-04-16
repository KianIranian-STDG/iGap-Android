package com.iGap.interfaces;

import com.iGap.proto.ProtoClientGetRoom;
import com.iGap.proto.ProtoGlobal;

public interface OnClientGetRoomResponse {

    void onClientGetRoomResponse(ProtoGlobal.Room room, ProtoClientGetRoom.ClientGetRoomResponse.Builder builder, String identity);

    void onError(int majorCode, int minorCode);

    void onTimeOut();
}
