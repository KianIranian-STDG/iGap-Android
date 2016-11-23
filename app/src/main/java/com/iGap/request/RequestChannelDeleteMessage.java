
package com.iGap.request;

import com.iGap.proto.ProtoChannelDeleteMessage;

public class RequestChannelDeleteMessage {

    public void channelDeleteMessage(long roomId, long messageId) {

        ProtoChannelDeleteMessage.ChannelDeleteMessage.Builder builder = ProtoChannelDeleteMessage.ChannelDeleteMessage.newBuilder();
        builder.setRoomId(roomId);
        builder.setMessageId(messageId);

        RequestWrapper requestWrapper = new RequestWrapper(411, builder);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
