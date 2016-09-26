package com.iGap.module;

import com.iGap.interface_package.OnGroupChatSendMessageResponse;
import com.iGap.proto.ProtoGlobal;
import com.iGap.proto.ProtoGroupSendMessage;
import com.iGap.request.RequestChatSendMessage;

/**
 * Created by Alireza Eskandarpour Shoferi (meNESS) on 9/5/2016.
 */

/**
 * util for chat send messages
 * useful for having callback from different activities
 */
public class GroupChatSendMessageUtil implements OnGroupChatSendMessageResponse {
    private OnGroupChatSendMessageResponse onChatSendMessageResponse;

    ProtoGroupSendMessage.GroupSendMessage.Builder chatSendMessage;

    public GroupChatSendMessageUtil newBuilder(ProtoGlobal.RoomMessageType messageType, long roomId) {
        chatSendMessage = ProtoGroupSendMessage.GroupSendMessage.newBuilder();
        chatSendMessage.setMessageType(messageType);
        chatSendMessage.setRoomId(roomId);
        return this;
    }

    public GroupChatSendMessageUtil message(String value) {
        chatSendMessage.setMessage(value);
        return this;
    }

    public GroupChatSendMessageUtil contact(ProtoGlobal.RoomMessageContact value) {
        chatSendMessage.setContact(value);
        return this;
    }

    public GroupChatSendMessageUtil attachment(String value) {
        chatSendMessage.setAttachment(value);
        return this;
    }

    public GroupChatSendMessageUtil location(ProtoGlobal.RoomMessageLocation value) {
        chatSendMessage.setLocation(value);
        return this;
    }

    public GroupChatSendMessageUtil log(ProtoGlobal.RoomMessageLog value) {
        chatSendMessage.setLog(value);
        return this;
    }

    public void setOnChatSendMessageResponse(OnGroupChatSendMessageResponse response) {
        this.onChatSendMessageResponse = response;
    }

    public void sendMessage(String fakeMessageIdAsIdentity) {
        new RequestChatSendMessage().newBuilder(chatSendMessage.getMessageType(), chatSendMessage.getRoomId())
                .message(chatSendMessage.getMessage()).attachment(chatSendMessage.getAttachment()).location(chatSendMessage.getLocation()).log(chatSendMessage.getLog()).sendMessage(fakeMessageIdAsIdentity);
    }

    @Override
    public void onMessageUpdated(long messageId, ProtoGlobal.RoomMessageStatus status, String identity, ProtoGroupSendMessage.GroupSendMessageResponse.Builder roomMessage) {
        if (onChatSendMessageResponse != null) {
            onChatSendMessageResponse.onMessageUpdated(messageId, status, identity, roomMessage);
        }
    }

    @Override
    public void onReceiveChatMessage(String message, String messageType, ProtoGroupSendMessage.GroupSendMessageResponse.Builder roomMessage) {
        if (onChatSendMessageResponse != null) {
            onChatSendMessageResponse.onReceiveChatMessage(message, messageType, roomMessage);
        }
    }
}
