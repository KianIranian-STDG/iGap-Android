package com.iGap.interfaces;

public interface OnMessageReceive {

    /**
     * message that reached from server
     */
    void onMessage(long roomId, long startMessageId, long endMessageId, boolean gapReached);

    void onError(int majorCode, int minorCode);

}
