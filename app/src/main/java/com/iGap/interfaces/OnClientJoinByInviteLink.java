package com.iGap.interfaces;

public interface OnClientJoinByInviteLink {
    void onClientJoinByInviteLinkResponse();

    void onError(int majorCode, int minorCode);
}
