package com.iGap.module;

/**
 * Created by android3 on 8/2/2016.
 * <p>
 * all information need to show in contact list
 */
public class StructContactInfo {

    public String contactID = "";
    public boolean muteNotification = false;
    public MyType.OwnerShip ownerShip = MyType.OwnerShip.member;

    public String imageSource = "";
    public String viewDistanceColor = "";
    public MyType.ChatType contactType = MyType.ChatType.singleChat;
    public String contactName = "";
    public String lastmessage = "";
    public String contactTime = "";
    public int unreadMessag = 0;

}
