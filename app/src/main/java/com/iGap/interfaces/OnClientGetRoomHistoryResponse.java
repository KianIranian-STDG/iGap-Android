package com.iGap.interfaces;

public interface OnClientGetRoomHistoryResponse {
    void onGetRoomHistory(long roomId, long startMessageId, long endMessageId);

    void onGetRoomHistoryError(int majorCode, int minorCode);
}
