// Copyright (c) 2016, iGap - www.iGap.im
// iGap is a Hybrid instant messaging service .
// RooyeKhat Media Co . - www.RooyeKhat.co
// All rights reserved.

package com.iGap.interfaces;

import com.iGap.proto.ProtoGlobal;
import com.iGap.proto.ProtoResponse;

public interface OnUserInfoResponse {
    void onUserInfo(ProtoGlobal.RegisteredUser user, ProtoResponse.Response response);

    void onUserInfoTimeOut();

    void onUserInfoError();

}
