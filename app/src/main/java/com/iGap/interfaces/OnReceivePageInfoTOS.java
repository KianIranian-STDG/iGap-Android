package com.iGap.interfaces;

public interface OnReceivePageInfoTOS {

    void onReceivePageInfo(String body);

    void onError(int majorCode, int minorCode);
}
