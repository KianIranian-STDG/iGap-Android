package com.iGap.interfaces;

import com.iGap.proto.ProtoChannelGetMessagesStats;

import java.util.List;

public interface OnChannelGetMessagesStats {
    void onChannelGetMessagesStats(List<ProtoChannelGetMessagesStats.ChannelGetMessagesStatsResponse.Stats> statsList);

    void onError(int majorCode, int minorCode);
}
