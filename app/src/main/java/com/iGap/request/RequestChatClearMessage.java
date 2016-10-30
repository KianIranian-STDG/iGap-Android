package com.iGap.request;

import com.iGap.proto.ProtoChatClearMessage;

public class RequestChatClearMessage {

    public void chatClearMessage(long roomId, long lastMessageId) {

        ProtoChatClearMessage.ChatClearMessage.Builder chatClearMessage =
            ProtoChatClearMessage.ChatClearMessage.newBuilder();
        chatClearMessage.setRoomId(roomId);
        chatClearMessage.setClearId(lastMessageId);

        RequestWrapper requestWrapper = new RequestWrapper(205, chatClearMessage);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}

