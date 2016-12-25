package com.iGap.interfaces;

import com.iGap.proto.ProtoGlobal;

public interface OnChannelAddMessageReaction {
    void onChannelAddMessageReaction(long roomId, long messageId, String reactionCounterLabel, ProtoGlobal.RoomMessageReaction reaction, long forwardedMessageId);

    void onError(int majorCode, int minorCode);
}
