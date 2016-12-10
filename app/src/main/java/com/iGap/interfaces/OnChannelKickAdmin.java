package com.iGap.interfaces;

public interface OnChannelKickAdmin {

    void onChannelKickAdmin(long roomId, long memberId);

    void onError(int majorCode, int minorCode);

    void onTimeOut();
}
