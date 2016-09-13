package com.iGap.helper;

/**
 * Created by Alireza Eskandarpour Shoferi (meNESS) on 9/6/2016.
 */

import android.text.format.DateUtils;

import com.iGap.module.MyType;
import com.iGap.module.StructChatInfo;
import com.iGap.module.StructMessageInfo;
import com.iGap.proto.ProtoChatSendMessage;
import com.iGap.proto.ProtoClientGetRoom;
import com.iGap.proto.ProtoGlobal;
import com.iGap.realm.RealmRoom;
import com.iGap.realm.RealmRoomMessage;
import com.iGap.realm.RealmUserInfo;
import com.iGap.realm.enums.RoomType;

import io.realm.Realm;

/**
 * helper methods while working with proto builders
 * note: when any field of classes was changed, update this helper.
 */
public final class HelperProtoBuilder {
    private HelperProtoBuilder() throws InstantiationException {
        throw new InstantiationException("This class is not for instantiation.");
    }

    public static StructMessageInfo convert(ProtoChatSendMessage.ChatSendMessageResponse.Builder builder) {
        Realm realm = Realm.getDefaultInstance();
        long userId = realm.where(RealmUserInfo.class).findFirst().getUserId();
        StructMessageInfo messageInfo = new StructMessageInfo();
        messageInfo.status = builder.getRoomMessage().getStatus().toString();
        messageInfo.messageID = Long.toString(builder.getRoomMessage().getMessageId());
        messageInfo.messageType = builder.getRoomMessage().getMessageType();
        // TODO: 9/8/2016 [Alireza Eskandarpour Shoferi] inja bayad createTime bezari ke felan server nemide.
        messageInfo.time = builder.getRoomMessage().getUpdateTime() * DateUtils.SECOND_IN_MILLIS;
        messageInfo.messageText = builder.getRoomMessage().getMessage();
        messageInfo.senderID = Long.toString(builder.getRoomMessage().getUserId());
        if (builder.getRoomMessage().getUserId() == userId) {
            messageInfo.sendType = MyType.SendType.send;
        } else if (builder.getRoomMessage().getUserId() != userId) {
            messageInfo.sendType = MyType.SendType.recvive;
        }
        return messageInfo;
    }

    /**
     * convert ProtoClientGetRoom.ClientGetRoomResponse.Builder to StructChatInfo
     *
     * @param builder ProtoClientGetRoom.ClientGetRoomResponse.Builder
     * @return StructChatInfo
     */
    public static StructChatInfo convert(ProtoClientGetRoom.ClientGetRoomResponse.Builder builder) {
        StructChatInfo chatInfo = new StructChatInfo();
        chatInfo.chatId = builder.getRoom().getId();
        chatInfo.chatTitle = builder.getRoom().getTitle();
        chatInfo.chatType = convert(builder.getRoom().getType());
        switch (builder.getRoom().getType()) {
            case CHANNEL:
                chatInfo.memberCount = builder.getRoom().getChannelRoom().getParticipantsCountLabel();
                break;
            case CHAT:
                chatInfo.memberCount = "1";
                break;
            case GROUP:
                chatInfo.memberCount = builder.getRoom().getGroupRoom().getParticipantsCountLabel();
                break;
        }
        chatInfo.muteNotification = false;
        chatInfo.ownerShip = MyType.OwnerShip.member;
        chatInfo.color = builder.getRoom().getColor();
        Realm realm = Realm.getDefaultInstance();
        RealmRoom room = realm.where(RealmRoom.class).equalTo("id", builder.getRoom().getId()).findFirst();
        if (room != null) {
            RealmRoomMessage roomMessage = realm.where(RealmRoomMessage.class).equalTo("messageId", room.getLastMessageId()).findFirst();
            if (roomMessage != null) {
                chatInfo.lastMessageTime = roomMessage.getUpdateTime();
                chatInfo.lastmessage = roomMessage.getMessage();
                chatInfo.unreadMessagesCount = room.getUnreadCount();
            }
        }
        realm.close();

        return chatInfo;
    }

    public static RoomType convert(ProtoGlobal.Room.Type type) {
        switch (type) {
            case CHANNEL:
                return RoomType.CHANNEL;
            case CHAT:
                return RoomType.CHAT;
            case GROUP:
                return RoomType.GROUP;
            case UNRECOGNIZED:
                return null;
            default:
                return null;
        }
    }
}
