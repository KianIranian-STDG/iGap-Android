package com.iGap.module;

import com.iGap.proto.ProtoClientGetRoom;
import com.iGap.realm.RealmRoom;
import com.iGap.realm.RealmRoomMessage;
import com.iGap.realm.enums.RoomType;

import io.realm.Realm;

/**
 * chat struct info
 * used for each chat in main chats list
 */
public class StructChatInfo {

    public long chatId;
    public boolean muteNotification = false;
    public MyType.OwnerShip ownerShip = MyType.OwnerShip.member;

    public String imageSource = "";
    public String color = "";
    public RoomType chatType = RoomType.CHAT;
    public String chatTitle = "";
    public String lastmessage = "";
    public long lastMessageTime;
    public boolean lastMessageSenderIsMe;
    public int unreadMessagesCount = 0;
    public String memberCount = "1";
    public String initials = "";
    public String lastMessageStatus = "";
    public String description = "";

    public String fileName = "";
    public String fileMime = "";
    public String fileSize = "";
    public String filePath = "";

    public String fileInfo = "";
    public String filePic = "";

    public MyType.FileState fileState = MyType.FileState.notDownload;

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
                chatInfo.lastMessageStatus = roomMessage.getStatus();
            }
        }
        realm.close();

        return chatInfo;
    }
}
