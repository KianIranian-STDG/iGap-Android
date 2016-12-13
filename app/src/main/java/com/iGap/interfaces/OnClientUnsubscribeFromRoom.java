package com.iGap.interfaces;

public interface OnClientUnsubscribeFromRoom {
    void onClientUnsubscribeFromRoom();

    void onError(int majorCode, int minorCode);
}
