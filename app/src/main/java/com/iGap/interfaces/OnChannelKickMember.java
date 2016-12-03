package com.iGap.interfaces;

public interface OnChannelKickMember {

    void onChannelKickMember(long roomId, long memberId);

    void onError(int majorCode, int minorCode);

    void onTimeOut();
}
