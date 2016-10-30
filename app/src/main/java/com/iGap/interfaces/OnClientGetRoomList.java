// Copyright (c) 2016, iGap - www.iGap.im
// iGap is a Hybrid instant messaging service .
// RooyeKhat Media Co . - www.RooyeKhat.co
// All rights reserved.

package com.iGap.interfaces;

import com.iGap.proto.ProtoGlobal;
import com.iGap.proto.ProtoResponse;

public interface OnClientGetRoomList {

    void onClientGetRoomList(ProtoGlobal.Room rooms, ProtoResponse.Response response);
}
