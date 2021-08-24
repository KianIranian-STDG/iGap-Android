package net.iGap.observers.interfaces;

public interface OnUserProfileSetBioResponse {

    void onUserProfileBioResponse(String bio);

    void error(int majorCode, int minorCode);

    void timeOut();
}
