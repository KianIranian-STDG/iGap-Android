package com.iGap.interfaces;

public interface OnChannelAddMessageReaction {
    void onChannelAddMessageReaction(String reactionCounterLabel);

    void onError(int majorCode, int minorCode);
}
