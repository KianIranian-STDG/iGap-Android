package com.iGap.interface_package;

public interface OnReceivePageInfoTOS {

    void onReceivePageInfo(String body);

    void onError(int majorCode, int minorCode);
}
