package com.iGap.module;

import com.iGap.interfaces.OnChatSendMessageResponse;
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
    RequestChatSendMessage requestChatSendMessage;
    RequestGroupSendMessage requestGroupSendMessage;
    ProtoGlobal.Room.Type roomType;
    private OnChatSendMessageResponse onChatSendMessageResponse;

    public ChatSendMessageUtil newBuilder(ProtoGlobal.Room.Type roomType,
        ProtoGlobal.RoomMessageType messageType, long roomId) {
        this.roomType = roomType;

        if (roomType == ProtoGlobal.Room.Type.CHAT) {
            requestChatSendMessage = new RequestChatSendMessage().newBuilder(messageType, roomId);
        } else if (roomType == ProtoGlobal.Room.Type.GROUP) {
            requestGroupSendMessage = new RequestGroupSendMessage().newBuilder(messageType, roomId);
        }
        return this;
    }

    public ChatSendMessageUtil message(String value) {
        if (roomType == ProtoGlobal.Room.Type.CHAT) {
            requestChatSendMessage.message(value);
        } else if (roomType == ProtoGlobal.Room.Type.GROUP) {
            requestGroupSendMessage.message(value);
        }
        return this;
    }

    public ChatSendMessageUtil attachment(String value) {
        if (roomType == ProtoGlobal.Room.Type.CHAT) {
            requestChatSendMessage.attachment(value);
        } else if (roomType == ProtoGlobal.Room.Type.GROUP) {
            requestGroupSendMessage.attachment(value);
        }
        return this;
    }

    public ChatSendMessageUtil contact(ProtoGlobal.RoomMessageContact value) {
        if (roomType == ProtoGlobal.Room.Type.CHAT) {
            requestChatSendMessage.contact(value);
        } else if (roomType == ProtoGlobal.Room.Type.GROUP) {
            requestGroupSendMessage.contact(value);
        }
        return this;
    }

    public ChatSendMessageUtil contact(String firstName, String lastName, String phoneNumber) {
        ProtoGlobal.RoomMessageContact.Builder value = ProtoGlobal.RoomMessageContact.newBuilder();
        value.setFirstName(firstName);
        value.setLastName(lastName);
        //value.addEmail();
        //value.setNickname();
        value.addPhone(phoneNumber);

        ProtoGlobal.RoomMessageContact built = value.build();

        if (roomType == ProtoGlobal.Room.Type.CHAT) {
            requestChatSendMessage.contact(built);
        } else if (roomType == ProtoGlobal.Room.Type.GROUP) {
            requestGroupSendMessage.contact(built);
        }
        return this;
    }

    public ChatSendMessageUtil location(ProtoGlobal.RoomMessageLocation value) {
        if (roomType == ProtoGlobal.Room.Type.CHAT) {
            requestChatSendMessage.location(value);
        } else if (roomType == ProtoGlobal.Room.Type.GROUP) {
            requestGroupSendMessage.location(value);
        }
        return this;
    }

    public ChatSendMessageUtil log(ProtoGlobal.RoomMessageLog value) {
        if (roomType == ProtoGlobal.Room.Type.CHAT) {
            requestChatSendMessage.log(value);
        } else if (roomType == ProtoGlobal.Room.Type.GROUP) {
            requestGroupSendMessage.log(value);
        }
        return this;
    }

    public void setOnChatSendMessageResponse(OnChatSendMessageResponse response) {
        this.onChatSendMessageResponse = response;
    }

    public void sendMessage(String fakeMessageIdAsIdentity) {
        if (roomType == ProtoGlobal.Room.Type.CHAT) {
            requestChatSendMessage.sendMessage(fakeMessageIdAsIdentity);
        } else if (roomType == ProtoGlobal.Room.Type.GROUP) {
            requestGroupSendMessage.sendMessage(fakeMessageIdAsIdentity);
        }
    }

    @Override
    public void onMessageUpdate(long roomId, long messageId, ProtoGlobal.RoomMessageStatus status,
        String identity, ProtoGlobal.RoomMessage roomMessage) {
        if (onChatSendMessageResponse != null) {
            onChatSendMessageResponse.onMessageUpdate(roomId, messageId, status, identity,
                roomMessage);
        }
    }

    @Override public void onMessageReceive(long roomId, String message, String messageType,
        ProtoGlobal.RoomMessage roomMessage, ProtoGlobal.Room.Type roomType) {
        if (onChatSendMessageResponse != null) {
            onChatSendMessageResponse.onMessageReceive(roomId, message, messageType, roomMessage,
                roomType);
        }
    }
}
