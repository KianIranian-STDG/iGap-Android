package com.iGap.interfaces;

import com.iGap.proto.ProtoGlobal;

public interface OnGroupAvatarResponse {
    void onAvatarAdd(long roomId, ProtoGlobal.Avatar avatar);

    void onAvatarAddError();
}
