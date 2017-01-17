package com.iGap.interfaces;

public interface OnUserVerification {

    void onUserVerify(String token, boolean newUser);

    void onUserVerifyError(int majorCode, int minorCode, int time);
}
