package com.iGap.module;

/**
 * Created by android3 on 8/6/2016.
 * <p>
 * all information need to show for chat list
 */
public class StructChatInfo {

    public String senderAvatar = "";  // this use for show avater in group
    public String senderName = "";
    public String senderID = "";
    public String seen = "1";
    public String channelLink = "";
    public int isChenged = 0;

    public String forwardMessageFrom = "";
    public MyType.MessageType messageType = MyType.MessageType.none;
    public MyType.SendType sendType = MyType.SendType.send;

    public String replayFrom = "";
    public String replayMessage = "";
    public String replayPicturePath = "";

    public String messag = "";
    public String fileName = "";
    public String fileMime = "";
    public String fileSize = "";
    public String filePath = "";
    public boolean isSelected = false;


}
