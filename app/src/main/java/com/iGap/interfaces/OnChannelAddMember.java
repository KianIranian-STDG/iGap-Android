package com.iGap.interfaces;

import com.iGap.proto.ProtoGlobal;

public interface OnChannelAddMember {

    void onChannelAddMember(Long roomId, Long UserId, ProtoGlobal.ChannelRoom.Role role);

    void onError(int majorCode, int minorCode);

    void onTimeOut();

}
