package com.iGap.module;

/**
 * chat struct info
 * used for each chat in main chats list
 */
public class StructChatInfo {

    public String contactID = "";
    public boolean muteNotification = false;
    public MyType.OwnerShip ownerShip = MyType.OwnerShip.member;
    public int whatChange = 0;

    public String imageSource = "";
    public String viewDistanceColor = "";
    public MyType.ChatType contactType = MyType.ChatType.singleChat;
    public String contactName = "";
    public String lastmessage = "";
    public String lastSeen = "";
    public int unreadMessag = 0;
    public String memberCount = "1";

}
