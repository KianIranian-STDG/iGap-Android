package com.iGap.interfaces;

import com.iGap.proto.ProtoGlobal;

/**
 * Created by Rahmani on 11/15/2016.
 */

public interface UpdateListAfterKick {

    void updateList(long memberId, ProtoGlobal.GroupRoom.Role role);
}
