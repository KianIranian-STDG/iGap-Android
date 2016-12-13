
package com.iGap.request;

import com.iGap.proto.ProtoChannelGetMessagesStats;

import java.util.ArrayList;

public class RequestChannelGetMessagesStats {

    public void channelGetMessagesStats(long roomId, ArrayList<Long> messageIds) {

        ProtoChannelGetMessagesStats.ChannelGetMessagesStats.Builder builder = ProtoChannelGetMessagesStats.ChannelGetMessagesStats.newBuilder();
        builder.setRoomId(roomId);
        for (long messageId : messageIds) {
            builder.addMessageId(messageId);
        }

        RequestWrapper requestWrapper = new RequestWrapper(423, builder);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
