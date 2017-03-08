package com.iGap.interfaces;

import com.iGap.proto.ProtoGlobal;
import java.util.List;

public interface OnClientSearchRoomHistory {

    void onClientSearchRoomHistory(int totalCount, int notDeletedCount, List<ProtoGlobal.RoomMessage> resultList, String identity);

    void onError(int majorCode, int minorCode, String identity);

}
