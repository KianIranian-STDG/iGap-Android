// Copyright (c) 2016, iGap - www.iGap.im
// iGap is a Hybrid instant messaging service .
// RooyeKhat Media Co . - www.RooyeKhat.co
// All rights reserved.

package com.iGap.interfaces;

import com.iGap.proto.ProtoGlobal;

public interface OnChatGetRoom {

    /**
     * roomId that exist in server
     *
     * @param roomId long roomId
     */

    void onChatGetRoom(long roomId);

    /**
     * room that exist in server with complete info
     *
     * @param room ProtoGlobal.Room
     */
    void onChatGetRoomCompletely(ProtoGlobal.Room room);

    void onChatGetRoomTimeOut();

    void onChatGetRoomError(int majorCode, int minorCode);
}
