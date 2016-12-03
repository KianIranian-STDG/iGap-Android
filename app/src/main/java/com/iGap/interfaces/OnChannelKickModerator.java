package com.iGap.interfaces;

public interface OnChannelKickModerator {

    void onChannelKickModerator(long roomId, long memberId);

    void onError(int majorCode, int minorCode);

    void onTimeOut();
}
