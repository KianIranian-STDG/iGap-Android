package com.iGap.interfaces;

public interface OnChannelLeft {

    void onChannelLeft(long roomId, long memberId);

    void onError(int majorCode, int minorCode);

    void onTimeOut();

}
