package com.iGap.interfaces;

public interface OnGroupRevokeLink {
    void onGroupRevokeLink(long roomId, String inviteLink, String inviteToken);

    void onError(int majorCode, int minorCode);

    void onTimeOut();
}
