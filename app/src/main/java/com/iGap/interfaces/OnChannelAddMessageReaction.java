package com.iGap.interfaces;

import com.iGap.proto.ProtoGlobal;

public interface OnChannelAddMessageReaction {
    void onChannelAddMessageReaction(long roomId, long messageId, int reactionCounterLabel, ProtoGlobal.RoomMessageReaction reaction);

    void onError(int majorCode, int minorCode);
}
