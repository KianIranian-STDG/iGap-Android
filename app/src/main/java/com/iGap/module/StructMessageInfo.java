package com.iGap.module;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.format.DateUtils;

import com.iGap.proto.ProtoGlobal;
import com.iGap.realm.RealmRoomMessage;
import com.iGap.realm.RealmUserInfo;

import io.realm.Realm;

/**
 * chat message struct info
 * used for chat messages
 */
public class StructMessageInfo implements Parcelable {

    public StructMessageInfo(String messageID, String senderID, String status, ProtoGlobal.RoomMessageType messageType, MyType.SendType sendType, MyType.FileState fileState, String fileName, String fileMime, String filePic, String filePath, long fileSize, byte[] fileHash, long time) {
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
        if (this.attachment != null) {
            this.attachment.localPath = filePath;
        } else {
            this.attachment = new StructMessageAttachment();
            this.attachment.localPath = filePath;
        }
        this.fileSize = fileSize;
        this.fileHash = fileHash;
        this.time = time;
    }

    public StructMessageInfo(String messageID, String senderID, String status, ProtoGlobal.RoomMessageType messageType, MyType.SendType sendType, MyType.FileState fileState, String fileName, String fileMime, String filePic, String filePath, long fileSize, byte[] fileHash, long time, StructMessageInfo replayObject) {
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
        if (this.attachment != null) {
            this.attachment.localPath = filePath;
        } else {
            this.attachment = new StructMessageAttachment();
            this.attachment.localPath = filePath;
        }
        this.fileSize = fileSize;
        this.fileHash = fileHash;
        this.time = time;
        this.replayFrom = replayObject.senderName;
        this.replayMessage = replayObject.messageText;
        this.replayPicturePath = replayObject.filePic;
    }

    public boolean isSenderMe() {
        Realm realm = Realm.getDefaultInstance();
        try {
            return Long.parseLong(senderID) == realm.where(RealmUserInfo.class).findFirst().getUserId();
        } finally {
            realm.close();
        }
    }

    public StructMessageInfo(String messageID, String senderID, String status, ProtoGlobal.RoomMessageType messageType, MyType.SendType sendType, MyType.FileState fileState, String filePath, long time) {
        this.messageID = messageID;
        this.senderID = senderID;
        this.status = status;
        this.messageType = messageType;
        this.sendType = sendType;
        this.fileState = fileState;
        this.filePath = filePath;
        if (this.attachment != null) {
            this.attachment.localPath = filePath;
        } else {
            this.attachment = new StructMessageAttachment();
            this.attachment.localPath = filePath;
        }
        this.time = time;
    }

    public StructMessageInfo(String messageID, String senderID, String status, ProtoGlobal.RoomMessageType messageType, MyType.SendType sendType, MyType.FileState fileState, String filePath, long time, StructMessageInfo replayObject) {
        this.messageID = messageID;
        this.senderID = senderID;
        this.status = status;
        this.messageType = messageType;
        this.sendType = sendType;
        this.fileState = fileState;
        this.filePath = filePath;
        if (this.attachment != null) {
            this.attachment.localPath = filePath;
        } else {
            this.attachment = new StructMessageAttachment();
            this.attachment.localPath = filePath;
        }
        this.time = time;
        this.replayFrom = replayObject.senderName;
        this.replayMessage = replayObject.messageText;
        this.replayPicturePath = replayObject.filePic;
    }

    public String messageID = "1";

    public String senderAvatar = "";  // useful for displaying avatar in group chat type
    public String senderName = "";
    public String senderID = "";
    // TODO: 9/15/2016 [Alireza Eskandarpour Shoferi] need sender avatar bg color
    public String senderColor = "";

    public boolean isTimeMessage() {
        return senderID.equalsIgnoreCase("-1");
    }

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
    public String filePic = "";
    public String filePath = "";
    public long fileSize;
    // used for uploading process and getting item from adapter by file hash
    public byte[] fileHash;
    public int uploadProgress;
    public StructMessageAttachment attachment;

    public StructMessageAttachment getAttachment() {
        return attachment;
    }

    public void setAttachment(StructMessageAttachment attachment) {
        this.attachment = attachment;
    }

    public long time;

    public boolean hasAttachment() {
        return attachment != null;
    }

    public StructMessageInfo() {
    }

    public static StructMessageInfo convert(ProtoGlobal.RoomMessage message) {
        Realm realm = Realm.getDefaultInstance();
        long userId = realm.where(RealmUserInfo.class).findFirst().getUserId();
        StructMessageInfo messageInfo = new StructMessageInfo();
        messageInfo.status = message.getStatus().toString();
        messageInfo.messageID = Long.toString(message.getMessageId());
        messageInfo.messageType = message.getMessageType();
        messageInfo.time = message.getUpdateTime() * DateUtils.SECOND_IN_MILLIS;
        messageInfo.messageText = message.getMessage();
        messageInfo.senderID = Long.toString(message.getUserId());
        messageInfo.attachment = StructMessageAttachment.convert(message.getAttachment());
        messageInfo.uploadProgress = messageInfo.attachment.token != null && !messageInfo.attachment.token.isEmpty() ? 100 : 0;
        if (message.getUserId() == userId) {
            messageInfo.sendType = MyType.SendType.send;
        } else if (message.getUserId() != userId) {
            messageInfo.sendType = MyType.SendType.recvive;
        }
        return messageInfo;
    }

    public static StructMessageInfo convert(RealmRoomMessage roomMessage) {
        Realm realm = Realm.getDefaultInstance();
        long userId = realm.where(RealmUserInfo.class).findFirst().getUserId();
        StructMessageInfo messageInfo = new StructMessageInfo();
        messageInfo.status = roomMessage.getStatus();
        messageInfo.messageID = Long.toString(roomMessage.getMessageId());
        messageInfo.isEdited = roomMessage.isEdited();
        messageInfo.messageType = ProtoGlobal.RoomMessageType.valueOf(roomMessage.getMessageType());
        messageInfo.time = roomMessage.getUpdateTime();
        if (roomMessage.getAttachment() != null) {
            messageInfo.attachment = StructMessageAttachment.convert(roomMessage.getAttachment());
            messageInfo.uploadProgress = messageInfo.attachment.token != null && !messageInfo.attachment.token.isEmpty() ? 100 : 0;
        }
        messageInfo.messageText = roomMessage.getMessage();
        messageInfo.senderID = Long.toString(roomMessage.getUserId());
        if (roomMessage.getUserId() == userId) {
            messageInfo.sendType = MyType.SendType.send;
        } else if (roomMessage.getUserId() != userId) {
            messageInfo.sendType = MyType.SendType.recvive;
        }
        return messageInfo;
    }

    public static StructMessageInfo convert(RealmRoomMessage roomMessage, String replaySenderName, String replaySenderMessage, String replayFilePic) {
        Realm realm = Realm.getDefaultInstance();
        long userId = realm.where(RealmUserInfo.class).findFirst().getUserId();
        StructMessageInfo messageInfo = new StructMessageInfo();
        messageInfo.status = roomMessage.getStatus();
        messageInfo.messageID = Long.toString(roomMessage.getMessageId());
        messageInfo.isEdited = roomMessage.isEdited();
        messageInfo.messageType = ProtoGlobal.RoomMessageType.valueOf(roomMessage.getMessageType());
        messageInfo.time = roomMessage.getUpdateTime();
        messageInfo.messageText = roomMessage.getMessage();
        messageInfo.senderID = Long.toString(roomMessage.getUserId());
        messageInfo.replayFrom = replaySenderName;
        if (roomMessage.getAttachment() != null) {
            messageInfo.attachment = StructMessageAttachment.convert(roomMessage.getAttachment());
            messageInfo.uploadProgress = messageInfo.attachment.token != null && !messageInfo.attachment.token.isEmpty() ? 100 : 0;
        }
        messageInfo.replayPicturePath = replayFilePic;
        messageInfo.replayMessage = replaySenderMessage;
        if (roomMessage.getUserId() == userId) {
            messageInfo.sendType = MyType.SendType.send;
        } else if (roomMessage.getUserId() != userId) {
            messageInfo.sendType = MyType.SendType.recvive;
        }
        return messageInfo;
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
        dest.writeString(this.filePic);
        dest.writeString(this.filePath);
        dest.writeLong(this.fileSize);
        dest.writeByteArray(this.fileHash);
        dest.writeInt(this.uploadProgress);
        dest.writeParcelable(this.attachment, flags);
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
        this.filePic = in.readString();
        this.filePath = in.readString();
        this.fileSize = in.readLong();
        this.fileHash = in.createByteArray();
        this.uploadProgress = in.readInt();
        this.attachment = in.readParcelable(StructMessageAttachment.class.getClassLoader());
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
