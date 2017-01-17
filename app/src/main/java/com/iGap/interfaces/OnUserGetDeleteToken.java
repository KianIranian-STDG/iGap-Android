package com.iGap.interfaces;

public interface OnUserGetDeleteToken {
    void onUserGetDeleteToken(int resendDelay, String tokenRegex, String tokenLength);

    void onUserGetDeleteError(int majorCode, int minorCode, int time);
}
