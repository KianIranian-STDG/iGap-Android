package net.iGap.observers.interfaces;

public interface OnUserProfileUpdateBio {
    void onUserProfileUpdateBio(String bio) ;

    void Error(int majorCode, int minorCode, int time);

    void timeOut();
}
