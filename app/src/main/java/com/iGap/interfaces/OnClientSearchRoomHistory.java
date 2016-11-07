package com.iGap.interfaces;

import com.iGap.proto.ProtoGlobal;

public interface OnClientSearchRoomHistory {

    void onClientSearchRoomHistory(int roomId, int name, String description, ProtoGlobal.GroupRoom.Role role);
}
