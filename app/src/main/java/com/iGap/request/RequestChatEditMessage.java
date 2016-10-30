package com.iGap.request;

import com.iGap.proto.ProtoChatEditMessage;

public class RequestChatEditMessage {

    public void chatEditMessage(long roomId, long messageId, String message) {

        ProtoChatEditMessage.ChatEditMessage.Builder chatEditMessage =
            ProtoChatEditMessage.ChatEditMessage.newBuilder();
        chatEditMessage.setRoomId(roomId);
        chatEditMessage.setMessageId(messageId);
        chatEditMessage.setMessage(message);

        RequestWrapper requestWrapper = new RequestWrapper(203, chatEditMessage);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}

