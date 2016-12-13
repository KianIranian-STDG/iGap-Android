package com.iGap.interfaces;

public interface OnClientSubscribeToRoom {
    void onClientSubscribeToRoom();

    void onError(int majorCode, int minorCode);
}
