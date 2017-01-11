package com.iGap.interfaces;

public interface OnUserSessionTerminate {
    void onUserSessionTerminate(Long messageid);

    void onTimeOut();

    void onError();
}
