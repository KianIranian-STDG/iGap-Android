// Copyright (c) 2016, iGap - www.iGap.im
// iGap is a Hybrid instant messaging service .
// RooyeKhat Media Co . - www.RooyeKhat.co
// All rights reserved.

package com.iGap.interface_package;

import com.iGap.proto.ProtoGlobal;
import com.iGap.proto.ProtoResponse;

public interface OnUserProfileGenderResponse {

    void onUserProfileEmailResponse(ProtoGlobal.Gender gender, ProtoResponse.Response response);
}
