package com.iGap.interfaces;

public interface OnClientJoinByUsername {
    void onClientJoinByUsernameResponse();

    void onError(int majorCode, int minorCode);
}
