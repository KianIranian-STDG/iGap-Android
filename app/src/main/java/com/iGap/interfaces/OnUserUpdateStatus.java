package com.iGap.interfaces;

import com.iGap.proto.ProtoUserUpdateStatus;

public interface OnUserUpdateStatus {

    void onUserUpdateStatus(long userId, ProtoUserUpdateStatus.UserUpdateStatus.Status status);
}
