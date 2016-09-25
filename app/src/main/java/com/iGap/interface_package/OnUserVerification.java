package com.iGap.interface_package;

public interface OnUserVerification {

    void onUserVerify(String token, boolean newUser);

    void onUserVerifyError(int majorCode, int minorCode);

}
