package com.iGap.interfaces;

public interface OnUserDelete {
    void onUserDeleteResponse();

    void Error(int majorCode, int minorCode);

    void TimeOut();
}
