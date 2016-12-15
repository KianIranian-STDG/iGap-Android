package com.iGap.interfaces;

import com.iGap.proto.ProtoGlobal;

public interface OnSetActionInRoom {
    void onSetAction(long roomId, long userId, ProtoGlobal.ClientAction clientAction);
}
