package com.iGap.interfaces;

import com.iGap.proto.ProtoGlobal;
import java.util.List;

public interface OnClientGetRoomHistoryResponse {
    void onGetRoomHistory(long roomId, List<ProtoGlobal.RoomMessage> messages, int count);

    void onGetRoomHistoryError(int majorCode, int minorCode);
}
