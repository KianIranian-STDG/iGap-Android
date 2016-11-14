package com.iGap.interfaces;

import com.iGap.proto.ProtoGlobal;

public interface OnChatConvertToGroup {

    void onChatConvertToGroup(long roomId, String name, String description, ProtoGlobal.GroupRoom.Role role);

    void Error(int majorCode, int minorCode);

    void timeOut();
}
