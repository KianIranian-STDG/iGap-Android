package com.iGap.interfaces;

import com.iGap.proto.ProtoGlobal;

public interface OnClientCheckInviteLink {
    void onClientCheckInviteLinkResponse(ProtoGlobal.Room room);

    void onError(int majorCode, int minorCode);
}
