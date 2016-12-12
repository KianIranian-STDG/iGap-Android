package com.iGap.interfaces;

import com.iGap.proto.ProtoGroupCheckUsername;

public interface OnGroupCheckUsername {
    void onGroupCheckUsername(ProtoGroupCheckUsername.GroupCheckUsernameResponse.Status status);

    void onError(int majorCode, int minorCode);
}
