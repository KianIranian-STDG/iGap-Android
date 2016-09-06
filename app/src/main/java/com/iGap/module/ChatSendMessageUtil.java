package com.iGap.module;

import com.iGap.interface_package.OnChatSendMessageResponse;
import com.iGap.proto.ProtoChatSendMessage;
import com.iGap.proto.ProtoGlobal;
import com.iGap.request.RequestChatSendMessage;

/**
 * Created by Alireza Eskandarpour Shoferi (meNESS) on 9/5/2016.
 */

/**
 * util for chat send messages
 * useful for having callback from different activities
 */
public class ChatSendMessageUtil implements OnChatSendMessageResponse {
    private OnChatSendMessageResponse onChatSendMessageResponse;

    ProtoChatSendMessage.ChatSendMessage.Builder chatSendMessage;

    public ChatSendMessageUtil newBuilder(ProtoGlobal.RoomMessageType messageType, long roomId) {
        chatSendMessage = ProtoChatSendMessage.ChatSendMessage.newBuilder();
        chatSendMessage.setMessageType(messageType);
        chatSendMessage.setRoomId(roomId);
        return this;
    }

    public ChatSendMessageUtil message(String value) {
        chatSendMessage.setMessage(value);
        return this;
    }

    public ChatSendMessageUtil attachment(String value) {
        chatSendMessage.setAttachment(value);
        return this;
    }

    public ChatSendMessageUtil location(ProtoGlobal.RoomMessageLocation value) {
        chatSendMessage.setLocation(value);
        return this;
    }

    public ChatSendMessageUtil log(ProtoGlobal.RoomMessageLog value) {
        chatSendMessage.setLog(value);
        return this;
    }

    public void setOnChatSendMessageResponse(OnChatSendMessageResponse response) {
        this.onChatSendMessageResponse = response;
    }

    public void sendMessage(String fakeMessageIdAsIdentity) {
        new RequestChatSendMessage().newBuilder(ProtoGlobal.RoomMessageType.TEXT, chatSendMessage.getRoomId())
                .message(chatSendMessage.getMessage()).sendMessage(fakeMessageIdAsIdentity);
    }

    @Override
    public void onMessageUpdated(long messageId, ProtoGlobal.RoomMessageStatus status, String identity, ProtoChatSendMessage.ChatSendMessageResponse.Builder roomMessage) {
        if (onChatSendMessageResponse != null) {
            onChatSendMessageResponse.onMessageUpdated(messageId, status, identity, roomMessage);
        }
    }

    @Override
    public void onReceiveChatMessage(String message, String messageType, ProtoChatSendMessage.ChatSendMessageResponse.Builder roomMessage) {
        if (onChatSendMessageResponse != null) {
            onChatSendMessageResponse.onReceiveChatMessage(message, messageType, roomMessage);
        }
    }
}
