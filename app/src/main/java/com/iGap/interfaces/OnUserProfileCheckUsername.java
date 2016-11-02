// Copyright (c) 2016, iGap - www.iGap.im
// iGap is a Hybrid instant messaging service .
// RooyeKhat Media Co . - www.RooyeKhat.co
// All rights reserved.

package com.iGap.interfaces;

import com.iGap.proto.ProtoUserProfileCheckUsername;

public interface OnUserProfileCheckUsername {

    void OnUserProfileCheckUsername(
        ProtoUserProfileCheckUsername.UserProfileCheckUsernameResponse.Status status);
}
