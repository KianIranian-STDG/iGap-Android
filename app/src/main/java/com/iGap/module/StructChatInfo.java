package com.iGap.module;

import com.iGap.realm.enums.RoomType;

/**
 * chat struct info
 * used for each chat in main chats list
 */
public class StructChatInfo {

    public String chatId = "";
    public boolean muteNotification = false;
    public MyType.OwnerShip ownerShip = MyType.OwnerShip.member;
    public int whatChange = 0;

    public String imageSource = "";
    public String viewDistanceColor = "";
    public RoomType chatType = RoomType.CHAT;
    public String chatTitle = "";
    public String lastmessage = "";
    public String lastSeen = "";
    public int unreadMessag = 0;
    public String memberCount = "1";

}
