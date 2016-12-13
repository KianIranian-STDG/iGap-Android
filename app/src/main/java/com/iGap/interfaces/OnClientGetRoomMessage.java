package com.iGap.interfaces;

import com.iGap.proto.ProtoGlobal;

public interface OnClientGetRoomMessage {
    void onClientGetRoomMessageResponse(ProtoGlobal.RoomMessage roomMessage);

    void onError(int majorCode, int minorCode);
}
