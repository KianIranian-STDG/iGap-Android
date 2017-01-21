// Copyright (c) 2016, iGap - www.iGap.im
// iGap is a Hybrid instant messaging service .
// RooyeKhat Media Co . - www.RooyeKhat.co
// All rights reserved.

package com.iGap.interfaces;

import com.iGap.proto.ProtoGlobal;

public interface OnUserProfileGetGender {

    void onUserProfileGetGender(ProtoGlobal.Gender gender);

    void onUserProfileGetGenderError(ProtoGlobal.Gender gender);

    void onUserProfileGetGenderTimeOut(ProtoGlobal.Gender gender);
}
