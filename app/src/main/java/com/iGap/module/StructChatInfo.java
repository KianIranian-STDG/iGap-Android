package com.iGap.module;

import com.iGap.proto.ProtoGlobal;
import com.iGap.realm.RealmRoom;
import com.iGap.realm.RealmRoomFields;
import com.iGap.realm.RealmRoomMessage;
import com.iGap.realm.RealmRoomMessageFields;
import com.iGap.realm.enums.RoomType;

import io.realm.Realm;

/**
 * chat struct info
 * used for each chat in main chats list
 */
public class StructChatInfo {

    public long chatId;
    public boolean muteNotification = false;

    public String color = "";
    public RoomType chatType = RoomType.CHAT;
    public String chatTitle = "";
    public long lastMessageId;
    public long lastMessageTime;
    public boolean lastMessageSenderIsMe;
    public int unreadMessagesCount = 0;
    public boolean readOnly;
    public String memberCount = "1";
    public String initials = "";
    public String lastMessageStatus = "";
    public String description = "";
    public int avatarCount;
    public StructMessageAttachment avatar;
    public long ownerId;
    public boolean userInfoAlreadyRequested;

    public String fileName = "";
    public String fileSize = "";
    public String filePath = "";
    public StructDownloadAttachment downloadAttachment;

    /**
     * convert ProtoClientGetRoom.ClientGetRoomResponse.Builder to StructChatInfo
     *
     * @param builder ProtoClientGetRoom.ClientGetRoomResponse.Builder
     * @return StructChatInfo
     */
    public static StructChatInfo convert(ProtoGlobal.Room builder) {
        StructChatInfo chatInfo = new StructChatInfo();
        chatInfo.chatId = builder.getId();
        chatInfo.chatTitle = builder.getTitle();
        chatInfo.chatType = RoomType.convert(builder.getType());
        chatInfo.initials = builder.getInitials();
        switch (builder.getType()) {
            case CHAT:
                chatInfo.memberCount = "1";
                chatInfo.avatar = StructMessageAttachment.convert(builder.getChatRoom().getPeer().getAvatar());
                chatInfo.ownerId = builder.getChatRoom().getPeer().getId();
                break;
            case GROUP:
                chatInfo.memberCount = builder.getGroupRoom().getParticipantsCountLabel();
                chatInfo.description = builder.getGroupRoom().getDescription();
                chatInfo.avatarCount = builder.getChannelRoom().getAvatarCount();
                chatInfo.avatar = StructMessageAttachment.convert(builder.getGroupRoom().getAvatar().getFile());
                break;
            case CHANNEL:
                chatInfo.memberCount = builder.getChannelRoom().getParticipantsCountLabel();
                chatInfo.description = builder.getChannelRoom().getDescription();
                chatInfo.avatarCount = builder.getGroupRoom().getAvatarCount();
                chatInfo.avatar = StructMessageAttachment.convert(builder.getChannelRoom().getAvatar().getFile());
                break;
        }
        chatInfo.readOnly = builder.getReadOnly();
        chatInfo.muteNotification = false;
        chatInfo.color = builder.getColor();
        chatInfo.lastMessageId = builder.getLastMessage().getMessageId();
        chatInfo.lastMessageTime = builder.getLastMessage().getUpdateTime();
        chatInfo.lastMessageStatus = builder.getLastMessage().getStatus().toString();
        chatInfo.unreadMessagesCount = builder.getUnreadCount();
        Realm realm = Realm.getDefaultInstance();
        RealmRoom room = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, builder.getId()).findFirst();
        if (room != null) {
            RealmRoomMessage roomMessage = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.MESSAGE_ID, room.getLastMessageId()).findFirst();
            if (roomMessage != null) {
                chatInfo.lastMessageId = roomMessage.getMessageId();
                chatInfo.lastMessageTime = roomMessage.getUpdateTime();
                chatInfo.lastMessageStatus = roomMessage.getStatus();
                chatInfo.unreadMessagesCount = room.getUnreadCount();
            }
        }
        realm.close();

        return chatInfo;
    }
}
