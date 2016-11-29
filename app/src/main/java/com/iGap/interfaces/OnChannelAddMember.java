package com.iGap.interfaces;

import com.iGap.proto.ProtoGlobal;

/**
 * Created by Rahmani on 11/29/2016.
 */

public interface OnChannelAddMember {

    void onChannelAddMember(Long RoomId, Long UserId, ProtoGlobal.ChannelRoom.Role role);

    void onError(int majorCode, int minorCode);

    void onTimeOut();

}
