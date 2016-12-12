// Copyright (c) 2016, iGap - www.iGap.im
// iGap is a Hybrid instant messaging service .
// RooyeKhat Media Co . - www.RooyeKhat.co
// All rights reserved.

package com.iGap.interfaces;

import com.iGap.proto.ProtoGlobal;

/**
 * just use this callback for update item in group chats
 */

public interface OnUserInfoItemResponse {

    void onUserInfoItem(ProtoGlobal.RegisteredUser user, String identity);

    void onUserInfoTimeOut();

    void onUserInfoError(int majorCode, int minorCode);

}
