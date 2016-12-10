package com.iGap.interfaces;

import com.iGap.proto.ProtoGlobal;

public interface OnChannelAvatarAdd {
    void onAvatarAdd(long roomId, ProtoGlobal.Avatar avatar);
}
