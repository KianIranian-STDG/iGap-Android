package com.iGap.module;

import com.iGap.realm.enums.RoomType;

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
    public int unreadMessagesCount = 0;
    public String memberCount = "1";
    public String initials = "";

    public String fileName = "";
    public String fileMime = "";
    public String fileSize = "";
    public String filePath = "";

    public String fileInfo = "";
    public String filePic = "";

    public MyType.FileState fileState = MyType.FileState.notDownload;
}
