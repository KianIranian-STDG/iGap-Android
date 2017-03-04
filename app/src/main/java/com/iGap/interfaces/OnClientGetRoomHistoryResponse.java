package com.iGap.interfaces;

import com.iGap.proto.ProtoGlobal;
import java.util.List;

public interface OnClientGetRoomHistoryResponse {
    void onGetRoomHistory(long roomId, List<ProtoGlobal.RoomMessage> roomMessages);

    void onGetRoomHistoryError(int majorCode, int minorCode);
}
