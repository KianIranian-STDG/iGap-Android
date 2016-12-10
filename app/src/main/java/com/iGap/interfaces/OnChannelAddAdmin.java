package com.iGap.interfaces;

public interface OnChannelAddAdmin {

    void onChannelAddAdmin(long roomId, long memberId);

    void onError(int majorCode, int minorCode);

    void onTimeOut();
}
