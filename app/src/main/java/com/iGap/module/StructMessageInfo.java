package com.iGap.module;

import android.os.Parcel;
import android.os.Parcelable;

import com.iGap.proto.ProtoGlobal;

import java.util.ArrayList;

/**
 * chat message struct info
 * used for chat messages
 */
public class StructMessageInfo implements Parcelable {

    public String messageID = "1";
    public String senderAvatar = "";  // this use for show avater in group
    public String senderName = "";
    public String senderID = "";

    public String status = ProtoGlobal.RoomMessageStatus.SENDING.toString();
    public String channelLink = "";
    public String initials;

    public boolean isChange = false;
    public ArrayList<String> allChanges = new ArrayList<>();
    public boolean isSelected = false;

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
    public long time;


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
        dest.writeString(this.status);
        dest.writeString(this.channelLink);
        dest.writeByte(this.isChange ? (byte) 1 : (byte) 0);
        dest.writeStringList(this.allChanges);
        dest.writeByte(this.isSelected ? (byte) 1 : (byte) 0);
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
        dest.writeLong(this.time);
    }

    public StructMessageInfo() {
    }

    protected StructMessageInfo(Parcel in) {
        this.messageID = in.readString();
        this.senderAvatar = in.readString();
        this.senderName = in.readString();
        this.senderID = in.readString();
        this.status = in.readString();
        this.channelLink = in.readString();
        this.isChange = in.readByte() != 0;
        this.allChanges = in.createStringArrayList();
        this.isSelected = in.readByte() != 0;
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
