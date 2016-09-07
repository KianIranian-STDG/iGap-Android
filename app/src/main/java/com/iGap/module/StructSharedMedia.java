package com.iGap.module;

import com.iGap.proto.ProtoGlobal;

import java.io.Serializable;

/**
 * Created by android3 on 9/4/2016.
 */
public class StructSharedMedia implements Serializable {

    public boolean isSelected = false;
    public ProtoGlobal.RoomMessageType messgeType = ProtoGlobal.RoomMessageType.IMAGE;
    public String tumpnail = "";
    public String filePath = "";
    public String fileUrl = "";
    public String fileName = "";
    public String fileTime = "";
    public ProtoGlobal.Room.Type chatType = ProtoGlobal.Room.Type.CHAT;
    public String chatId = "";


}
