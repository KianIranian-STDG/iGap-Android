package com.iGap.module;

import com.iGap.proto.ProtoClientGetRoom;
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
    public String lastmessage = "";
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
    public static StructChatInfo convert(ProtoClientGetRoom.ClientGetRoomResponse.Builder builder) {
        StructChatInfo chatInfo = new StructChatInfo();
        chatInfo.chatId = builder.getRoom().getId();
        chatInfo.chatTitle = builder.getRoom().getTitle();
        chatInfo.chatType = RoomType.convert(builder.getRoom().getType());
        chatInfo.initials = builder.getRoom().getInitials();
        switch (builder.getRoom().getType()) {
            case CHAT:
                chatInfo.memberCount = "1";
                chatInfo.avatar = StructMessageAttachment.convert(builder.getRoom().getChatRoom().getPeer().getAvatar());
                chatInfo.ownerId = builder.getRoom().getChatRoom().getPeer().getId();
                break;
            case GROUP:
                chatInfo.memberCount = builder.getRoom().getGroupRoom().getParticipantsCountLabel();
                chatInfo.description = builder.getRoom().getGroupRoom().getDescription();
                chatInfo.avatarCount = builder.getRoom().getChannelRoom().getAvatarCount();
                chatInfo.avatar = StructMessageAttachment.convert(builder.getRoom().getGroupRoom().getAvatar().getFile());
                break;
            case CHANNEL:
                chatInfo.memberCount = builder.getRoom().getChannelRoom().getParticipantsCountLabel();
                chatInfo.description = builder.getRoom().getChannelRoom().getDescription();
                chatInfo.avatarCount = builder.getRoom().getGroupRoom().getAvatarCount();
                chatInfo.avatar = StructMessageAttachment.convert(builder.getRoom().getChannelRoom().getAvatar().getFile());
                break;
        }
        chatInfo.readOnly = builder.getRoom().getReadOnly();
        chatInfo.muteNotification = false;
        chatInfo.color = builder.getRoom().getColor();
        chatInfo.lastmessage = builder.getRoom().getLastMessage().getStatus().toString();
        chatInfo.lastMessageTime = builder.getRoom().getLastMessage().getUpdateTime();
        chatInfo.lastMessageStatus = builder.getRoom().getLastMessage().getStatus().toString();
        chatInfo.unreadMessagesCount = builder.getRoom().getUnreadCount();
        Realm realm = Realm.getDefaultInstance();
        RealmRoom room = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, builder.getRoom().getId()).findFirst();
        if (room != null) {
            RealmRoomMessage roomMessage = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.MESSAGE_ID, room.getLastMessageId()).findFirst();
            if (roomMessage != null) {
                chatInfo.lastmessage = roomMessage.getMessage();
                chatInfo.lastMessageTime = roomMessage.getUpdateTime();
                chatInfo.lastMessageStatus = roomMessage.getStatus();
                chatInfo.unreadMessagesCount = room.getUnreadCount();
            }
        }
        realm.close();

        return chatInfo;
    }
}
