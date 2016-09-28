package com.iGap.module;

import com.iGap.interface_package.OnChatSendMessageResponse;
import com.iGap.proto.ProtoChatSendMessage;
import com.iGap.proto.ProtoGlobal;
import com.iGap.request.RequestChatSendMessage;
import com.iGap.request.RequestGroupSendMessage;

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

    public ChatSendMessageUtil contact(ProtoGlobal.RoomMessageContact value) {
        chatSendMessage.setContact(value);
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

    public void sendMessage(ProtoGlobal.Room.Type roomType, String fakeMessageIdAsIdentity) {
        if (roomType == ProtoGlobal.Room.Type.CHAT) {
            new RequestChatSendMessage().newBuilder(chatSendMessage.getMessageType(), chatSendMessage.getRoomId())
                    .message(chatSendMessage.getMessage()).attachment(chatSendMessage.getAttachment()).location(chatSendMessage.getLocation()).log(chatSendMessage.getLog()).sendMessage(fakeMessageIdAsIdentity);
        } else if (roomType == ProtoGlobal.Room.Type.GROUP) {
            new RequestGroupSendMessage().newBuilder(chatSendMessage.getMessageType(), chatSendMessage.getRoomId())
                    .message(chatSendMessage.getMessage()).attachment(chatSendMessage.getAttachment()).location(chatSendMessage.getLocation()).log(chatSendMessage.getLog()).sendMessage(fakeMessageIdAsIdentity);
        }
    }

    @Override
    public void onMessageUpdate(long roomId, long messageId, ProtoGlobal.RoomMessageStatus status, String identity, ProtoGlobal.RoomMessage roomMessage) {
        if (onChatSendMessageResponse != null) {
            onChatSendMessageResponse.onMessageUpdate(roomId, messageId, status, identity, roomMessage);
        }
    }

    @Override
    public void onMessageReceive(long roomId, String message, String messageType, ProtoGlobal.RoomMessage roomMessage) {
        if (onChatSendMessageResponse != null) {
            onChatSendMessageResponse.onMessageReceive(roomId, message, messageType, roomMessage);
        }
    }
}
