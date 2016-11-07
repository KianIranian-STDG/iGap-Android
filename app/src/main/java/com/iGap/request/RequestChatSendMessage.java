package com.iGap.request;

import com.iGap.proto.ProtoChatSendMessage;
import com.iGap.proto.ProtoGlobal;

public class RequestChatSendMessage { //TODO [Saeed Mozaffari] [2016-08-29 1:02 PM] - change
    // builder to force use newBuilder
    ProtoChatSendMessage.ChatSendMessage.Builder chatSendMessage;

    public RequestChatSendMessage newBuilder(ProtoGlobal.RoomMessageType messageType, long roomId) {
        chatSendMessage = ProtoChatSendMessage.ChatSendMessage.newBuilder();
        chatSendMessage.setMessageType(messageType);
        chatSendMessage.setRoomId(roomId);
        return this;
    }

    public RequestChatSendMessage message(String value) {
        chatSendMessage.setMessage(value);
        return this;
    }

    public RequestChatSendMessage attachment(String value) {
        chatSendMessage.setAttachment(value);
        return this;
    }

    public RequestChatSendMessage location(ProtoGlobal.RoomMessageLocation value) {
        chatSendMessage.setLocation(value);
        return this;
    }

    public RequestChatSendMessage log(ProtoGlobal.RoomMessageLog value) {
        chatSendMessage.setLog(value);
        return this;
    }

    public RequestChatSendMessage contact(ProtoGlobal.RoomMessageContact value) {
        chatSendMessage.setContact(value);
        return this;
    }

    public RequestChatSendMessage forwardMessage(ProtoGlobal.RoomMessageForwardFrom forwardFrom) {
        chatSendMessage.setForwardFrom(forwardFrom);
        return this;
    }

    public RequestChatSendMessage sendMessage(String fakeMessageIdAsIdentity) {
        RequestWrapper requestWrapper =
                new RequestWrapper(201, chatSendMessage, fakeMessageIdAsIdentity);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return this;
    }
}

