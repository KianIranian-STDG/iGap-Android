package com.iGap.interfaces;

public interface OnChannelRevokeLink {
    void onChannelRevokeLink(long roomId, String inviteLink, String inviteToken);

    void onError(int majorCode, int minorCode);

    void onTimeOut();
}
