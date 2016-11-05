package com.iGap.module;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.format.DateUtils;
import android.view.View;
import com.iGap.proto.ProtoGlobal;
import com.iGap.realm.RealmRegisteredInfo;
import com.iGap.realm.RealmRegisteredInfoFields;
import com.iGap.realm.RealmRoomMessage;
import com.iGap.realm.RealmRoomMessageFields;
import com.iGap.realm.RealmUserInfo;
import com.iGap.realm.RealmUserInfoFields;
import io.realm.Realm;
import org.parceler.Parcels;

/**
 * chat message struct info
 * used for chat messages
 */
public class StructMessageInfo implements Parcelable {
    public View view = null;
    public String messageID = "1";
    public String senderID = "";
    public String senderColor = "";
    public boolean isEdited;
    public String status;
    public String initials;
    public String forwardMessageFrom;
    public ProtoGlobal.RoomMessageType messageType;
    public MyType.SendType sendType;
    public RealmRoomMessage replayTo;
    public String songArtist;
    public long songLength;
    public String messageText = "";
    public String fileMime = "";
    public String filePic = "";
    public String filePath = "";
    // used for uploading process and getting item from adapter by file hash
    public byte[] fileHash;
    public int uploadProgress;
    public StructMessageAttachment attachment;
    public StructDownloadAttachment downloadAttachment;
    public StructRegisteredInfo userInfo;
    public StructMessageAttachment senderAvatar;
    public long time;

    public StructMessageInfo(String messageID, String senderID, String messageText, String status,
        ProtoGlobal.RoomMessageType messageType, MyType.SendType sendType, String fileMime,
        String filePic, String localThumbnailPath, String localFilePath, byte[] fileHash,
        long time) {
        this.messageID = messageID;
        Realm realm = Realm.getDefaultInstance();
        RealmRegisteredInfo realmRegisteredInfo = realm.where(RealmRegisteredInfo.class)
            .equalTo(RealmRegisteredInfoFields.ID, Long.parseLong(senderID))
            .findFirst();
        this.senderAvatar = realmRegisteredInfo != null ? StructMessageAttachment.convert(
            realmRegisteredInfo.getLastAvatar()) : null;
        realm.close();
        this.senderID = senderID;
        this.status = status;
        this.messageType = messageType;
        this.sendType = sendType;
        this.fileMime = fileMime;
        this.filePic = filePic;
        this.filePath = localThumbnailPath;
        this.messageText = messageText;
        if (this.attachment == null) {
            this.attachment = new StructMessageAttachment();
        }
        this.attachment.setLocalThumbnailPath(Long.parseLong(messageID), localThumbnailPath);
        this.attachment.setLocalFilePath(Long.parseLong(messageID), localFilePath);
        this.fileHash = fileHash;
        this.time = time;
    }

    public StructMessageInfo(String messageID, String senderID, String status,
        ProtoGlobal.RoomMessageType messageType, MyType.SendType sendType, String fileMime,
        String filePic, String localThumbnailPath, String localFilePath, byte[] fileHash, long time,
        long replayToMessageId) {
        this.messageID = messageID;
        Realm realm = Realm.getDefaultInstance();
        RealmRegisteredInfo realmRegisteredInfo = realm.where(RealmRegisteredInfo.class)
            .equalTo(RealmRegisteredInfoFields.ID, Long.parseLong(senderID))
            .findFirst();
        this.senderAvatar = realmRegisteredInfo != null ? StructMessageAttachment.convert(
            realmRegisteredInfo.getLastAvatar()) : null;

        this.senderID = senderID;
        this.status = status;
        this.messageType = messageType;
        this.sendType = sendType;
        this.fileMime = fileMime;
        this.filePic = filePic;
        this.filePath = localThumbnailPath;
        if (this.attachment == null) {
            this.attachment = new StructMessageAttachment();
        }
        this.attachment.setLocalThumbnailPath(Long.parseLong(messageID), localThumbnailPath);
        this.attachment.setLocalFilePath(Long.parseLong(messageID), localFilePath);
        this.fileHash = fileHash;
        this.time = time;
        this.replayTo = realm.where(RealmRoomMessage.class)
            .equalTo(RealmRoomMessageFields.MESSAGE_ID, replayToMessageId)
            .findFirst();
        realm.close();
    }

    public StructMessageInfo(String messageID, String senderID, String status,
        ProtoGlobal.RoomMessageType messageType, MyType.SendType sendType,
        String localThumbnailPath, String localFilePath, long time) {
        this.messageID = messageID;
        Realm realm = Realm.getDefaultInstance();
        RealmRegisteredInfo realmRegisteredInfo = realm.where(RealmRegisteredInfo.class)
            .equalTo(RealmRegisteredInfoFields.ID, Long.parseLong(senderID))
            .findFirst();
        this.senderAvatar = realmRegisteredInfo != null ? StructMessageAttachment.convert(
            realmRegisteredInfo.getLastAvatar()) : null;
        realm.close();
        this.senderID = senderID;
        this.status = status;
        this.messageType = messageType;
        this.sendType = sendType;
        this.filePath = localThumbnailPath;
        if (this.attachment == null) {
            this.attachment = new StructMessageAttachment();
        }
        this.attachment.setLocalThumbnailPath(Long.parseLong(messageID), localThumbnailPath);
        this.attachment.setLocalFilePath(Long.parseLong(messageID), localFilePath);
        this.time = time;
    }

    public StructMessageInfo(String messageID, String messageText, String senderID, String status,
        ProtoGlobal.RoomMessageType messageType, MyType.SendType sendType,
        String localThumbnailPath, String localFilePath, long time) {
        this.messageID = messageID;
        Realm realm = Realm.getDefaultInstance();
        RealmRegisteredInfo realmRegisteredInfo = realm.where(RealmRegisteredInfo.class)
            .equalTo(RealmRegisteredInfoFields.ID, Long.parseLong(senderID))
            .findFirst();
        this.senderAvatar = realmRegisteredInfo != null ? StructMessageAttachment.convert(
            realmRegisteredInfo.getLastAvatar()) : null;
        realm.close();
        this.senderID = senderID;
        this.status = status;
        this.messageType = messageType;
        this.messageText = messageText;
        this.sendType = sendType;
        this.filePath = localThumbnailPath;
        if (this.attachment == null) {
            this.attachment = new StructMessageAttachment();
        }
        this.attachment.setLocalThumbnailPath(Long.parseLong(messageID), localThumbnailPath);
        this.attachment.setLocalFilePath(Long.parseLong(messageID), localFilePath);
        this.time = time;
    }

    public StructMessageInfo(String messageID, String senderID, String status,
        ProtoGlobal.RoomMessageType messageType, MyType.SendType sendType,
        String localThumbnailPath, String localFilePath, long time, long replayToMessageId) {
        this.messageID = messageID;
        Realm realm = Realm.getDefaultInstance();
        RealmRegisteredInfo realmRegisteredInfo = realm.where(RealmRegisteredInfo.class)
            .equalTo(RealmRegisteredInfoFields.ID, Long.parseLong(senderID))
            .findFirst();
        this.senderAvatar = realmRegisteredInfo != null ? StructMessageAttachment.convert(
            realmRegisteredInfo.getLastAvatar()) : null;
        this.senderID = senderID;
        this.status = status;
        this.messageType = messageType;
        this.sendType = sendType;
        this.filePath = localThumbnailPath;
        if (this.attachment == null) {
            this.attachment = new StructMessageAttachment();
        }
        this.attachment.setLocalThumbnailPath(Long.parseLong(messageID), localThumbnailPath);
        this.attachment.setLocalFilePath(Long.parseLong(messageID), localFilePath);
        this.time = time;
        this.replayTo = realm.where(RealmRoomMessage.class)
            .equalTo(RealmRoomMessageFields.MESSAGE_ID, replayToMessageId)
            .findFirst();
        realm.close();
    }

    public StructMessageInfo() {
    }

    public static StructMessageInfo buildForAudio(long messageID, long senderID,
        ProtoGlobal.RoomMessageStatus status, ProtoGlobal.RoomMessageType messageType,
        MyType.SendType sendType, long time, String messageText, String localThumbnailPath,
        String localFilePath, String songArtist, long songLength, long replayToMessageId) {
        StructMessageInfo info = new StructMessageInfo();

        info.messageID = Long.toString(messageID);
        Realm realm = Realm.getDefaultInstance();
        RealmRegisteredInfo realmRegisteredInfo = realm.where(RealmRegisteredInfo.class)
            .equalTo(RealmRegisteredInfoFields.ID, senderID)
            .findFirst();
        info.senderAvatar = realmRegisteredInfo != null ? StructMessageAttachment.convert(
            realmRegisteredInfo.getLastAvatar()) : null;
        info.senderID = Long.toString(senderID);
        info.status = status.toString();
        info.messageType = messageType;
        info.sendType = sendType;
        info.attachment = info.attachment == null ? new StructMessageAttachment() : info.attachment;
        info.attachment.setLocalThumbnailPath(messageID, localThumbnailPath);
        info.attachment.setLocalFilePath(messageID, localFilePath);
        info.time = time;
        info.messageText = messageText;
        info.replayTo = realm.where(RealmRoomMessage.class)
            .equalTo(RealmRoomMessageFields.MESSAGE_ID, replayToMessageId)
            .findFirst();
        realm.close();
        // audio exclusive
        info.songArtist = songArtist;
        info.songLength = songLength;

        return info;
    }

    public static StructMessageInfo buildForContact(long messageID, long senderID,
        MyType.SendType sendType, long time, ProtoGlobal.RoomMessageStatus status, String username,
        String firstName, String lastName, String number, long replayToMessageId) {
        StructMessageInfo info = new StructMessageInfo();

        info.messageID = Long.toString(messageID);
        info.senderID = Long.toString(senderID);
        Realm realm = Realm.getDefaultInstance();
        RealmRegisteredInfo realmRegisteredInfo = realm.where(RealmRegisteredInfo.class)
            .equalTo(RealmRegisteredInfoFields.ID, senderID)
            .findFirst();
        info.senderAvatar = realmRegisteredInfo != null ? StructMessageAttachment.convert(
            realmRegisteredInfo.getLastAvatar()) : null;
        info.status = status.toString();
        info.messageType = ProtoGlobal.RoomMessageType.CONTACT;
        info.sendType = sendType;
        info.time = time;
        if (replayToMessageId != -1) {
            info.replayTo = realm.where(RealmRoomMessage.class)
                .equalTo(RealmRoomMessageFields.MESSAGE_ID, replayToMessageId)
                .findFirst();
        }
        realm.close();

        // contact exclusive
        info.userInfo = new StructRegisteredInfo(lastName, firstName, number, username, senderID);
        return info;
    }

    public static StructMessageInfo convert(ProtoGlobal.RoomMessage message) {
        Realm realm = Realm.getDefaultInstance();
        long userId = realm.where(RealmUserInfo.class).findFirst().getUserId();
        StructMessageInfo messageInfo = new StructMessageInfo();
        messageInfo.status = message.getStatus().toString();
        messageInfo.messageID = Long.toString(message.getMessageId());
        if (message.getUserId() != userId) {
            RealmRegisteredInfo realmRegisteredInfo = realm.where(RealmRegisteredInfo.class)
                .equalTo(RealmRegisteredInfoFields.ID, message.getUserId())
                .findFirst();
            if (realmRegisteredInfo != null) {
                messageInfo.senderAvatar =
                    StructMessageAttachment.convert(realmRegisteredInfo.getLastAvatar());
                messageInfo.senderColor = realmRegisteredInfo.getColor();
                messageInfo.initials = realmRegisteredInfo.getInitials();
            }
        }
        messageInfo.messageType = message.getMessageType();
        messageInfo.time = message.getUpdateTime() * DateUtils.SECOND_IN_MILLIS;
        messageInfo.messageText = message.getMessage();
        messageInfo.senderID = Long.toString(message.getUserId());
        messageInfo.attachment = StructMessageAttachment.convert(message.getAttachment());
        if (message.getMessageType() == ProtoGlobal.RoomMessageType.CONTACT) {
            messageInfo.userInfo = StructRegisteredInfo.build(message.getContact());
        }
        messageInfo.uploadProgress =
            messageInfo.attachment.token != null && !messageInfo.attachment.token.isEmpty() ? 100
                : 0;
        if (message.getUserId() == userId) {
            messageInfo.sendType = MyType.SendType.send;
        } else if (message.getUserId() != userId) {
            messageInfo.sendType = MyType.SendType.recvive;
        }
        if (message.getForwardFrom() != null) {
            RealmUserInfo userInfo = realm.where(RealmUserInfo.class).equalTo(RealmUserInfoFields.USER_INFO.ID, message.getForwardFrom().getUserId())
                .findFirst();
            if (userInfo != null) {
                messageInfo.forwardMessageFrom = userInfo.getUserInfo().getDisplayName();
            }
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
        if (!roomMessage.isSenderMe()) {
            RealmRegisteredInfo realmRegisteredInfo = realm.where(RealmRegisteredInfo.class)
                .equalTo(RealmRegisteredInfoFields.ID, roomMessage.getUserId())
                .findFirst();
            if (realmRegisteredInfo != null) {
                messageInfo.senderAvatar =
                    StructMessageAttachment.convert(realmRegisteredInfo.getLastAvatar());
                messageInfo.senderColor = realmRegisteredInfo.getColor();
                messageInfo.initials = realmRegisteredInfo.getInitials();
            }
        }
        messageInfo.messageType = ProtoGlobal.RoomMessageType.valueOf(roomMessage.getMessageType());
        messageInfo.time = roomMessage.getUpdateTime();
        if (roomMessage.getAttachment() != null) {
            messageInfo.attachment = StructMessageAttachment.convert(roomMessage.getAttachment());
            messageInfo.uploadProgress =
                messageInfo.attachment.token != null && !messageInfo.attachment.token.isEmpty()
                    ? 100 : 0;
        }
        messageInfo.messageText = roomMessage.getMessage();
        messageInfo.senderID = Long.toString(roomMessage.getUserId());
        if (roomMessage.getUserId() == userId) {
            messageInfo.sendType = MyType.SendType.send;
        } else if (roomMessage.getUserId() != userId) {
            messageInfo.sendType = MyType.SendType.recvive;
        }
        if (roomMessage.getMessageType()
            .equalsIgnoreCase(ProtoGlobal.RoomMessageType.CONTACT.toString())) {
            messageInfo.userInfo = StructRegisteredInfo.build(roomMessage.getRoomMessageContact());
        }
        if (roomMessage.getForwardMessage() != null) {
            RealmUserInfo userInfo = realm.where(RealmUserInfo.class).equalTo(RealmUserInfoFields.USER_INFO.ID, roomMessage.getForwardMessage().getUserId())
                .findFirst();
            if (userInfo != null) {
                messageInfo.forwardMessageFrom = userInfo.getUserInfo().getDisplayName();
            }
        }

        messageInfo.replayTo = roomMessage.getReplyTo();

        return messageInfo;
    }

    public boolean isSenderMe() {
        Realm realm = Realm.getDefaultInstance();
        try {
            return Long.parseLong(senderID) == realm.where(RealmUserInfo.class)
                .findFirst()
                .getUserId();
        } finally {
            realm.close();
        }
    }

    public boolean isTimeMessage() {
        return senderID.equalsIgnoreCase("-1");
    }

    public StructMessageAttachment getAttachment() {
        return attachment;
    }

    public void setAttachment(StructMessageAttachment attachment) {
        this.attachment = attachment;
    }

    public boolean hasAttachment() {
        return attachment != null;
    }

    @Override public int describeContents() {
        return 0;
    }

    @Override public void writeToParcel(Parcel dest, int flags) {
        //dest.writeParcelable(this.view, flags);
        dest.writeString(this.messageID);
        dest.writeString(this.senderID);
        dest.writeString(this.senderColor);
        dest.writeByte(this.isEdited ? (byte) 1 : (byte) 0);
        dest.writeString(this.status);
        dest.writeString(this.initials);
        dest.writeString(this.forwardMessageFrom);
        dest.writeInt(this.messageType == null ? -1 : this.messageType.ordinal());
        dest.writeInt(this.sendType == null ? -1 : this.sendType.ordinal());
        dest.writeParcelable(Parcels.wrap(this.replayTo), flags);
        dest.writeString(this.songArtist);
        dest.writeLong(this.songLength);
        dest.writeString(this.messageText);
        dest.writeString(this.fileMime);
        dest.writeString(this.filePic);
        dest.writeString(this.filePath);
        dest.writeByteArray(this.fileHash);
        dest.writeInt(this.uploadProgress);
        dest.writeParcelable(this.attachment, flags);
        dest.writeParcelable(Parcels.wrap(this.downloadAttachment), flags);
        dest.writeParcelable(this.userInfo, flags);
        dest.writeParcelable(this.senderAvatar, flags);
        dest.writeLong(this.time);
    }

    protected StructMessageInfo(Parcel in) {
        //this.view = in.readParcelable(View.class.getClassLoader());
        this.messageID = in.readString();
        this.senderID = in.readString();
        this.senderColor = in.readString();
        this.isEdited = in.readByte() != 0;
        this.status = in.readString();
        this.initials = in.readString();
        this.forwardMessageFrom = in.readString();
        int tmpMessageType = in.readInt();
        this.messageType =
            tmpMessageType == -1 ? null : ProtoGlobal.RoomMessageType.values()[tmpMessageType];
        int tmpSendType = in.readInt();
        this.sendType = tmpSendType == -1 ? null : MyType.SendType.values()[tmpSendType];
        this.replayTo = Parcels.unwrap(in.readParcelable(RealmRoomMessage.class.getClassLoader()));
        this.songArtist = in.readString();
        this.songLength = in.readLong();
        this.messageText = in.readString();
        this.fileMime = in.readString();
        this.filePic = in.readString();
        this.filePath = in.readString();
        this.fileHash = in.createByteArray();
        this.uploadProgress = in.readInt();
        this.attachment = in.readParcelable(StructMessageAttachment.class.getClassLoader());
        this.downloadAttachment =
            in.readParcelable(StructDownloadAttachment.class.getClassLoader());
        this.userInfo = in.readParcelable(StructRegisteredInfo.class.getClassLoader());
        this.senderAvatar = in.readParcelable(StructMessageAttachment.class.getClassLoader());
        this.time = in.readLong();
    }

    public static final Parcelable.Creator<StructMessageInfo> CREATOR =
        new Parcelable.Creator<StructMessageInfo>() {
            @Override public StructMessageInfo createFromParcel(Parcel source) {
                return new StructMessageInfo(source);
            }

            @Override public StructMessageInfo[] newArray(int size) {
                return new StructMessageInfo[size];
            }
        };
}
