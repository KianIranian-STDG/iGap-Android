package com.iGap.request;

import com.iGap.proto.ProtoChannelEditMessage;

public class RequestChannelEditMessage {

    public void channelEditMessage(long roomId, long messageId, String message) {

        ProtoChannelEditMessage.ChannelEditMessage.Builder chatEditMessage = ProtoChannelEditMessage.ChannelEditMessage.newBuilder();
        chatEditMessage.setRoomId(roomId);
        chatEditMessage.setMessageId(messageId);
        chatEditMessage.setMessage(message);

        RequestWrapper requestWrapper = new RequestWrapper(425, chatEditMessage);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}

