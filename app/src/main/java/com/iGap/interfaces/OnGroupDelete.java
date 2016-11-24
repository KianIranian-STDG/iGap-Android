package com.iGap.interfaces;

public interface OnGroupDelete {
    void onGroupDelete(long roomId);

    void Error(int majorCode, int minorCode);

    void onTimeOut();

}
