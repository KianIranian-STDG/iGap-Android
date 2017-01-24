package com.iGap.interfaces;

import com.iGap.proto.ProtoGlobal;

public interface OnUserAvatarResponse {
    void onAvatarAdd(ProtoGlobal.Avatar avatar);

    void onAvatarAddTimeOut();
    void onAvatarError();
}
