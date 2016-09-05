package com.iGap.module;

import com.iGap.proto.ProtoGlobal;

import java.util.ArrayList;

/**
 * chat message struct info
 * used for chat messages
 */
public class StructMessageInfo {

    public String messageID = "1";
    public String senderAvatar = "";  // this use for show avater in group
    public String senderName = "";
    public String senderID = "";

    public String status = ProtoGlobal.RoomMessageStatus.SENDING.toString();
    public String channelLink = "";

    public boolean isChange = false;
    public ArrayList<String> allChanges = new ArrayList<>();
    public boolean isSelected = false;

    public String forwardMessageFrom = "";

    public MyType.MessageType messageType = MyType.MessageType.none;
    public MyType.SendType sendType = MyType.SendType.send;
    public MyType.FileState fileState = MyType.FileState.notDownload;

    public String replayFrom = "";
    public String replayMessage = "";
    public String replayPicturePath = "";

    public String messageText = "";

    public String fileName = "";
    public String fileMime = "";
    public String fileInfo = "";
    public String filePic = "";
    public String filePath = "";


}
