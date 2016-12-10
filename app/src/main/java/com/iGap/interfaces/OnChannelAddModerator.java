package com.iGap.interfaces;

public interface OnChannelAddModerator {

    void onChannelAddModerator(long roomId, long memberId);

    void onError(int majorCode, int minorCode);

    void onTimeOut();
}
