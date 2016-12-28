package com.iGap.module;

import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;

import com.iGap.proto.ProtoGlobal;
import com.iGap.realm.RealmRegisteredInfo;
import com.iGap.realm.RealmRegisteredInfoFields;
import com.iGap.realm.RealmRoom;
import com.iGap.realm.RealmRoomFields;
import com.iGap.realm.RealmRoomMessage;
import com.iGap.realm.RealmRoomMessageFields;
import com.iGap.realm.RealmUserInfo;

import org.parceler.Parcels;

import io.realm.Realm;

/**
 * chat message struct info
 * used for chat messages
 */
public class StructMessageInfo implements Parcelable {
    public static final Parcelable.Creator<StructMessageInfo> CREATOR =
            new Parcelable.Creator<StructMessageInfo>() {
                @Override
                public StructMessageInfo createFromParcel(Parcel source) {
                    return new StructMessageInfo(source);
                }

                @Override
                public StructMessageInfo[] newArray(int size) {
                    return new StructMessageInfo[size];
                }
            };
    public View view = null;
    public long roomId;
    public String messageID = "1";
    public String senderID = "";
    public String senderColor = "";
    public boolean isEdited;
    public String status;
    public String initials;
    public ProtoGlobal.RoomMessageType messageType;
    public MyType.SendType sendType;
    public RealmRoomMessage replayTo;
    public RealmRoomMessage forwardedFrom;
    public String songArtist;
    public long songLength;
    public String messageText = "";
    public String location;
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
    /*public int voteUp;
    public int voteDown;
    public int viewsLabel;*/
    public StructChannelExtra channelExtra;

    public StructMessageInfo() {
    }


    public StructMessageInfo(long roomId, String messageID, String senderID, String messageText, String status, ProtoGlobal.RoomMessageType messageType, MyType.SendType sendType, String fileMime, String filePic, String localThumbnailPath, String localFilePath, byte[] fileHash, long time) {
        this.roomId = roomId;
        this.messageID = messageID;

        Realm realm = Realm.getDefaultInstance();
        RealmRegisteredInfo realmRegisteredInfo = realm.where(RealmRegisteredInfo.class).equalTo(RealmRegisteredInfoFields.ID, Long.parseLong(senderID)).findFirst();
        this.senderAvatar = realmRegisteredInfo != null ? StructMessageAttachment.convert(realmRegisteredInfo.getLastAvatar()) : null;
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

    public StructMessageInfo(long roomId, String messageID, String senderID, String status, ProtoGlobal.RoomMessageType messageType, MyType.SendType sendType, String fileMime, String filePic, String localThumbnailPath, String localFilePath, byte[] fileHash, long time, long replayToMessageId) {
        this.roomId = roomId;
        this.messageID = messageID;
        Realm realm = Realm.getDefaultInstance();
        RealmRegisteredInfo realmRegisteredInfo = realm.where(RealmRegisteredInfo.class).equalTo(RealmRegisteredInfoFields.ID, Long.parseLong(senderID)).findFirst();
        this.senderAvatar = realmRegisteredInfo != null ? StructMessageAttachment.convert(realmRegisteredInfo.getLastAvatar()) : null;

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
        this.replayTo = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.MESSAGE_ID, replayToMessageId).findFirst();
        realm.close();
    }

    public StructMessageInfo(long roomId, String messageID, String senderID, String status, ProtoGlobal.RoomMessageType messageType, MyType.SendType sendType, String localThumbnailPath, String localFilePath, long time) {
        this.roomId = roomId;
        this.messageID = messageID;

        Realm realm = Realm.getDefaultInstance();
        RealmRegisteredInfo realmRegisteredInfo = realm.where(RealmRegisteredInfo.class).equalTo(RealmRegisteredInfoFields.ID, Long.parseLong(senderID)).findFirst();
        this.senderAvatar = realmRegisteredInfo != null ? StructMessageAttachment.convert(realmRegisteredInfo.getLastAvatar()) : null;
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

    public StructMessageInfo(long roomId, String messageID, String messageText, String senderID, String status, ProtoGlobal.RoomMessageType messageType, MyType.SendType sendType, String localThumbnailPath, String localFilePath, long time) {
        this.roomId = roomId;
        this.messageID = messageID;

        Realm realm = Realm.getDefaultInstance();
        RealmRegisteredInfo realmRegisteredInfo = realm.where(RealmRegisteredInfo.class).equalTo(RealmRegisteredInfoFields.ID, Long.parseLong(senderID)).findFirst();
        this.senderAvatar = realmRegisteredInfo != null ? StructMessageAttachment.convert(realmRegisteredInfo.getLastAvatar()) : null;
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

    public StructMessageInfo(long roomId, String messageID, String senderID, String status, ProtoGlobal.RoomMessageType messageType, MyType.SendType sendType, String localThumbnailPath, String localFilePath, long time, long replayToMessageId) {
        this.roomId = roomId;
        this.messageID = messageID;

        Realm realm = Realm.getDefaultInstance();
        RealmRegisteredInfo realmRegisteredInfo = realm.where(RealmRegisteredInfo.class).equalTo(RealmRegisteredInfoFields.ID, Long.parseLong(senderID)).findFirst();
        this.senderAvatar = realmRegisteredInfo != null ? StructMessageAttachment.convert(realmRegisteredInfo.getLastAvatar()) : null;

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
        this.replayTo = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.MESSAGE_ID, replayToMessageId).findFirst();
        realm.close();
    }

    protected StructMessageInfo(Parcel in) {
        //this.view = in.readParcelable(View.class.getClassLoader());
        this.roomId = in.readLong();
        this.messageID = in.readString();
        this.senderID = in.readString();
        this.senderColor = in.readString();
        this.isEdited = in.readByte() != 0;
        this.status = in.readString();
        this.initials = in.readString();
        int tmpMessageType = in.readInt();
        this.messageType = tmpMessageType == -1 ? null : ProtoGlobal.RoomMessageType.values()[tmpMessageType];
        int tmpSendType = in.readInt();
        this.sendType = tmpSendType == -1 ? null : MyType.SendType.values()[tmpSendType];
        this.replayTo = Parcels.unwrap(in.readParcelable(RealmRoomMessage.class.getClassLoader()));
        this.forwardedFrom = Parcels.unwrap(in.readParcelable(RealmRoomMessage.class.getClassLoader()));
        this.songArtist = in.readString();
        this.songLength = in.readLong();
        this.messageText = in.readString();
        this.fileMime = in.readString();
        this.filePic = in.readString();
        this.filePath = in.readString();
        this.fileHash = in.createByteArray();
        this.uploadProgress = in.readInt();
        this.attachment = in.readParcelable(StructMessageAttachment.class.getClassLoader());
        this.downloadAttachment = Parcels.unwrap(in.readParcelable(StructDownloadAttachment.class.getClassLoader()));
        this.userInfo = in.readParcelable(StructRegisteredInfo.class.getClassLoader());
        this.senderAvatar = in.readParcelable(StructMessageAttachment.class.getClassLoader());
        this.time = in.readLong();
        /*this.voteUp = in.readInt();
        this.voteDown = in.readInt();
        this.viewsLabel = in.readInt();*/
        this.channelExtra = Parcels.unwrap(in.readParcelable(StructChannelExtra.class.getClassLoader()));
    }

    public static StructMessageInfo buildForAudio(long roomId, long messageID, long senderID, ProtoGlobal.RoomMessageStatus status, ProtoGlobal.RoomMessageType messageType, MyType.SendType sendType, long time, String messageText, String localThumbnailPath, String localFilePath, String songArtist, long songLength, long replayToMessageId) {
        StructMessageInfo info = new StructMessageInfo();

        info.roomId = roomId;
        info.messageID = Long.toString(messageID);

        Realm realm = Realm.getDefaultInstance();
        RealmRegisteredInfo realmRegisteredInfo = realm.where(RealmRegisteredInfo.class).equalTo(RealmRegisteredInfoFields.ID, senderID).findFirst();
        info.senderAvatar = realmRegisteredInfo != null ? StructMessageAttachment.convert(realmRegisteredInfo.getLastAvatar()) : null;

        info.senderID = Long.toString(senderID);
        info.status = status.toString();
        info.messageType = messageType;
        info.sendType = sendType;
        info.attachment = info.attachment == null ? new StructMessageAttachment() : info.attachment;
        info.attachment.setLocalThumbnailPath(messageID, localThumbnailPath);
        info.attachment.setLocalFilePath(messageID, localFilePath);
        info.time = time;
        info.messageText = messageText;
        if (replayToMessageId != -1) {
            info.replayTo = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.MESSAGE_ID, replayToMessageId).findFirst();
        }
        realm.close();
        // audio exclusive
        info.songArtist = songArtist;
        info.songLength = songLength;

        return info;
    }

    public static StructMessageInfo buildForContact(long roomId, long messageID, long senderID, MyType.SendType sendType, long time, ProtoGlobal.RoomMessageStatus status, String firstName, String lastName, String number, long replayToMessageId) {
        StructMessageInfo info = new StructMessageInfo();
        info.roomId = roomId;
        info.messageID = Long.toString(messageID);
        info.senderID = Long.toString(senderID);

        Realm realm = Realm.getDefaultInstance();
        RealmRegisteredInfo realmRegisteredInfo = realm.where(RealmRegisteredInfo.class).equalTo(RealmRegisteredInfoFields.ID, senderID).findFirst();
        info.senderAvatar = realmRegisteredInfo != null ? StructMessageAttachment.convert(realmRegisteredInfo.getLastAvatar()) : null;

        info.status = status.toString();
        info.messageType = ProtoGlobal.RoomMessageType.CONTACT;
        info.sendType = sendType;
        info.time = time;
        info.messageText = firstName + " " + number;
        if (replayToMessageId != 0) {
            info.replayTo = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.MESSAGE_ID, replayToMessageId).findFirst();
        }
        realm.close();
        // contact exclusive
        info.userInfo = new StructRegisteredInfo(lastName, firstName, number, senderID);
        return info;
    }

    public static StructMessageInfo convert(RealmRoomMessage roomMessage) {
        Realm realm = Realm.getDefaultInstance();
        RealmUserInfo realmUserInfo = realm.where(RealmUserInfo.class).findFirst();
        long userId = realmUserInfo.getUserId();
        StructMessageInfo messageInfo = new StructMessageInfo();
        messageInfo.roomId = roomMessage.getRoomId();
        messageInfo.status = roomMessage.getStatus();
        messageInfo.messageID = Long.toString(roomMessage.getMessageId());
        messageInfo.isEdited = roomMessage.isEdited();
        if (!roomMessage.isSenderMe()) {
            RealmRegisteredInfo realmRegisteredInfo = realm.where(RealmRegisteredInfo.class).equalTo(RealmRegisteredInfoFields.ID, roomMessage.getUserId()).findFirst();
            if (realmRegisteredInfo != null) {
                messageInfo.senderAvatar = StructMessageAttachment.convert(realmRegisteredInfo.getLastAvatar());
                messageInfo.senderColor = realmRegisteredInfo.getColor();
                messageInfo.initials = realmRegisteredInfo.getInitials();
            }
        }
        messageInfo.messageType = roomMessage.getMessageType();
        messageInfo.time = roomMessage.getUpdateOrCreateTime();
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
        if (roomMessage.getMessageType() == ProtoGlobal.RoomMessageType.CONTACT) {
            messageInfo.userInfo = StructRegisteredInfo.build(roomMessage.getRoomMessageContact());
        }
        if (roomMessage.getForwardMessage() != null) {

            boolean showForward = false;

            RealmRegisteredInfo userInfo = realm.where(RealmRegisteredInfo.class).equalTo(RealmRegisteredInfoFields.ID, roomMessage.getForwardMessage().getUserId()).findFirst();
            if (userInfo != null) {
                showForward = true;
            } else {
                RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, roomMessage.getForwardMessage().getRoomId()).findFirst();
                if (realmRoom != null) {
                    showForward = true;
                }
            }

            if (showForward) {
                messageInfo.forwardedFrom = roomMessage.getForwardMessage();
                if (roomMessage.getForwardMessage().getAttachment() != null) {
                    messageInfo.attachment = StructMessageAttachment.convert(roomMessage.getForwardMessage().getAttachment());
                    messageInfo.uploadProgress = messageInfo.attachment.token != null && !messageInfo.attachment.token.isEmpty() ? 100 : 0;
                }
            }
        }
        if (roomMessage.getLocation() != null) {
            double lat = roomMessage.getLocation().getLocationLat();
            double lon = roomMessage.getLocation().getLocationLong();
            messageInfo.location = Double.toString(lat) + "," + Double.toString(lon);
        }

        if (roomMessage.getLog() != null) {
            messageInfo.senderID = "-1";
            //messageInfo.messageText = roomMessage.getLog().getType().toString();
            messageInfo.messageText = roomMessage.getLogMessage();
        }

        messageInfo.replayTo = roomMessage.getReplyTo();
        if (roomMessage.getChannelExtra() != null) {
            messageInfo.channelExtra = StructChannelExtra.convert(roomMessage.getChannelExtra());
        } else {
            messageInfo.channelExtra = new StructChannelExtra();
        }

        return messageInfo;
    }

    public boolean isSenderMe() {
        Realm realm = Realm.getDefaultInstance();
        try {
            return Long.parseLong(senderID) == realm.where(RealmUserInfo.class).findFirst().getUserId();
        } finally {
            realm.close();
        }
    }

    public boolean isTimeOrLogMessage() {
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        //dest.writeParcelable(this.view, flags);
        dest.writeLong(this.roomId);
        dest.writeString(this.messageID);
        dest.writeString(this.senderID);
        dest.writeString(this.senderColor);
        dest.writeByte(this.isEdited ? (byte) 1 : (byte) 0);
        dest.writeString(this.status);
        dest.writeString(this.initials);
        dest.writeInt(this.messageType == null ? -1 : this.messageType.ordinal());
        dest.writeInt(this.sendType == null ? -1 : this.sendType.ordinal());
        dest.writeParcelable(Parcels.wrap(this.replayTo), flags);
        dest.writeParcelable(Parcels.wrap(this.forwardedFrom), flags);
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
        /*dest.writeInt(this.voteUp);
        dest.writeInt(this.voteDown);
        dest.writeInt(this.viewsLabel);*/
        dest.writeParcelable(Parcels.wrap(this.channelExtra), flags);
    }
}
