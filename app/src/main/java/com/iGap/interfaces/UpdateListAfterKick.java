package com.iGap.interfaces;

import com.iGap.proto.ProtoGlobal;


public interface UpdateListAfterKick {
    void updateList(long memberId, ProtoGlobal.GroupRoom.Role role);
}
