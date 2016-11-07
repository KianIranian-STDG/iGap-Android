package com.iGap.request;

import com.iGap.proto.ProtoChatDeleteMessage;

public class RequestChatDeleteMessage {

    public void chatDeleteMessage(long roomId, long messageId) {

        ProtoChatDeleteMessage.ChatDeleteMessage.Builder chatDeleteMessage =
                ProtoChatDeleteMessage.ChatDeleteMessage.newBuilder();
        chatDeleteMessage.setRoomId(roomId);
        chatDeleteMessage.setMessageId(messageId);

        RequestWrapper requestWrapper = new RequestWrapper(204, chatDeleteMessage);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}

