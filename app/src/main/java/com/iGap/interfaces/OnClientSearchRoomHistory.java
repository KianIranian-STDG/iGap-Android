package com.iGap.interfaces;

import com.iGap.proto.ProtoGlobal;

import java.util.List;

public interface OnClientSearchRoomHistory {

    void onClientSearchRoomHistory(int totalCount, int notDeletedCount, List<ProtoGlobal.RoomMessage> resultList);

    void onError(int majorCode, int minorCode);

    void onTimeOut();

}
