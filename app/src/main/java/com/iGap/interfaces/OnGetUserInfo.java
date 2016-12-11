package com.iGap.interfaces;

import com.iGap.proto.ProtoGlobal;

/**
 * call this interface after read from RealmAvatar
 */

public interface OnGetUserInfo {
    void onGetUserInfo(ProtoGlobal.RegisteredUser registeredUser);
}
