package com.iGap.interfaces;

public interface OnReceiveInfoLocation {

    void onReceive(String isoCode, int callingCode, String countryName, String pattern,
                   String regex);

    void onError(int majorCode, int minorCode);
}
