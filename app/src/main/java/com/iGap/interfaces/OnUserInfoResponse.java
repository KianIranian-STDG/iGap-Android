// Copyright (c) 2016, iGap - www.iGap.im
// iGap is a Hybrid instant messaging service .
// RooyeKhat Media Co . - www.RooyeKhat.co
// All rights reserved.

package com.iGap.interfaces;

import com.iGap.proto.ProtoGlobal;

public interface OnUserInfoResponse {


    void onUserInfo(ProtoGlobal.RegisteredUser user, String identity);

    void onUserInfoTimeOut();

    void onUserInfoError(int majorCode, int minorCode);

}
