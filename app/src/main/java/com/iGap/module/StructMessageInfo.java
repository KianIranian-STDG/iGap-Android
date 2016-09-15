package com.iGap.module;

import android.os.Parcel;
import android.os.Parcelable;

import com.iGap.proto.ProtoGlobal;

/**
 * chat message struct info
 * used for chat messages
 */
public class StructMessageInfo implements Parcelable {

    public StructMessageInfo(String messageID, String senderID, String status, ProtoGlobal.RoomMessageType messageType, MyType.SendType sendType, MyType.FileState fileState, String fileName, String fileMime, String fileInfo, String filePic, String filePath, long fileSize, long time) {
        this.messageID = messageID;
        this.senderID = senderID;
        this.status = status;
        this.messageType = messageType;
        this.sendType = sendType;
        this.fileState = fileState;
        this.fileName = fileName;
        this.fileMime = fileMime;
        this.fileInfo = fileInfo;
        this.filePic = filePic;
        this.filePath = filePath;
        this.fileSize = fileSize;
        this.time = time;
    }

    public StructMessageInfo(String messageID, String senderID, String status, ProtoGlobal.RoomMessageType messageType, MyType.SendType sendType, MyType.FileState fileState, String fileName, String fileMime, String filePic, String filePath, long fileSize, long time) {
        this.messageID = messageID;
        this.senderID = senderID;
        this.status = status;
        this.messageType = messageType;
        this.sendType = sendType;
        this.fileState = fileState;
        this.fileName = fileName;
        this.fileMime = fileMime;
        this.filePic = filePic;
        this.filePath = filePath;
        this.time = time;
        this.fileSize = fileSize;
    }

    public StructMessageInfo(String messageID, String senderID, String status, ProtoGlobal.RoomMessageType messageType, MyType.SendType sendType, MyType.FileState fileState, String filePath, long time) {
        this.messageID = messageID;
        this.senderID = senderID;
        this.status = status;
        this.messageType = messageType;
        this.sendType = sendType;
        this.fileState = fileState;
        this.filePath = filePath;
        this.time = time;
    }

    public String messageID = "1";

    public String senderAvatar = "";  // useful for displaying avatar in group chat type
    public String senderName = "";
    public String senderID = "";
    // TODO: 9/15/2016 [Alireza Eskandarpour Shoferi] need sender avatar bg color
    public String senderColor = "";

    public boolean isEdited = false;

    public String status = ProtoGlobal.RoomMessageStatus.SENDING.toString();
    public String channelLink = "";
    public String initials;

    public String forwardMessageFrom = "";

    public ProtoGlobal.RoomMessageType messageType = ProtoGlobal.RoomMessageType.UNRECOGNIZED;
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
    public long fileSize;

    public long time;

    public StructMessageInfo() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.messageID);
        dest.writeString(this.senderAvatar);
        dest.writeString(this.senderName);
        dest.writeString(this.senderID);
        dest.writeString(this.senderColor);
        dest.writeByte(this.isEdited ? (byte) 1 : (byte) 0);
        dest.writeString(this.status);
        dest.writeString(this.channelLink);
        dest.writeString(this.initials);
        dest.writeString(this.forwardMessageFrom);
        dest.writeInt(this.messageType == null ? -1 : this.messageType.ordinal());
        dest.writeInt(this.sendType == null ? -1 : this.sendType.ordinal());
        dest.writeInt(this.fileState == null ? -1 : this.fileState.ordinal());
        dest.writeString(this.replayFrom);
        dest.writeString(this.replayMessage);
        dest.writeString(this.replayPicturePath);
        dest.writeString(this.messageText);
        dest.writeString(this.fileName);
        dest.writeString(this.fileMime);
        dest.writeString(this.fileInfo);
        dest.writeString(this.filePic);
        dest.writeString(this.filePath);
        dest.writeLong(this.fileSize);
        dest.writeLong(this.time);
    }

    protected StructMessageInfo(Parcel in) {
        this.messageID = in.readString();
        this.senderAvatar = in.readString();
        this.senderName = in.readString();
        this.senderID = in.readString();
        this.senderColor = in.readString();
        this.isEdited = in.readByte() != 0;
        this.status = in.readString();
        this.channelLink = in.readString();
        this.initials = in.readString();
        this.forwardMessageFrom = in.readString();
        int tmpMessageType = in.readInt();
        this.messageType = tmpMessageType == -1 ? null : ProtoGlobal.RoomMessageType.values()[tmpMessageType];
        int tmpSendType = in.readInt();
        this.sendType = tmpSendType == -1 ? null : MyType.SendType.values()[tmpSendType];
        int tmpFileState = in.readInt();
        this.fileState = tmpFileState == -1 ? null : MyType.FileState.values()[tmpFileState];
        this.replayFrom = in.readString();
        this.replayMessage = in.readString();
        this.replayPicturePath = in.readString();
        this.messageText = in.readString();
        this.fileName = in.readString();
        this.fileMime = in.readString();
        this.fileInfo = in.readString();
        this.filePic = in.readString();
        this.filePath = in.readString();
        this.fileSize = in.readLong();
        this.time = in.readLong();
    }

    public static final Parcelable.Creator<StructMessageInfo> CREATOR = new Parcelable.Creator<StructMessageInfo>() {
        @Override
        public StructMessageInfo createFromParcel(Parcel source) {
            return new StructMessageInfo(source);
        }

        @Override
        public StructMessageInfo[] newArray(int size) {
            return new StructMessageInfo[size];
        }
    };
}
