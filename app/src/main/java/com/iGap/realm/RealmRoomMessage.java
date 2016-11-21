package com.iGap.realm;

import android.text.format.DateUtils;

import com.iGap.module.SUID;
import com.iGap.module.enums.AttachmentFor;
import com.iGap.module.enums.LocalFileType;
import com.iGap.proto.ProtoGlobal;

import org.parceler.Parcel;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.RealmRoomMessageRealmProxy;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;

@Parcel(implementations = {RealmRoomMessageRealmProxy.class},
        value = Parcel.Serialization.BEAN,
        analyze = {RealmRoomMessage.class})
public class RealmRoomMessage extends RealmObject {
    @PrimaryKey
    private long messageId;
    @Index
    private long roomId;
    private long messageVersion;
    private String status;
    private long statusVersion;
    private String messageType;
    private String message;
    private RealmAttachment attachment;
    private long userId;
    private RealmRoomMessageLocation location;
    private RealmRoomMessageLog log;
    private RealmRoomMessageContact roomMessageContact;
    private boolean edited;
    private long createTime;
    private long updateTime;
    private boolean deleted;
    private RealmRoomMessage forwardMessage;
    private RealmRoomMessage replyTo;

    public static RealmRoomMessage putOrUpdate(ProtoGlobal.RoomMessage input, long roomId) {
        Realm realm = Realm.getDefaultInstance();

        RealmRoomMessage message = realm.where(RealmRoomMessage.class).equalTo(RealmRoomMessageFields.MESSAGE_ID, input.getMessageId()).findFirst();

        if (message == null) {
            message = realm.createObject(RealmRoomMessage.class, input.getMessageId());
        }
        message.setMessage(input.getMessage());
        message.setStatus(input.getStatus().toString());
        message.setUserId(input.getAuthor().getUser().getUserId());
        message.setRoomId(roomId);
        if (input.hasAttachment()) {
            message.setAttachment(RealmAttachment.build(input.getAttachment(), AttachmentFor.MESSAGE_ATTACHMENT));
        }
        message.setCreateTime(input.getCreateTime() * DateUtils.SECOND_IN_MILLIS);
        message.setDeleted(input.getDeleted());
        message.setEdited(input.getEdited());
        if (input.hasForwardFrom()) {
            message.setForwardMessage(RealmRoomMessage.putOrUpdate(input.getForwardFrom(), roomId));
        }
        message.setLocation(RealmRoomMessageLocation.build(input.getLocation()));
        message.setLog(RealmRoomMessageLog.build(input.getLog()));
        message.setMessageType(input.getMessageType());
        message.setMessageVersion(input.getMessageVersion());
        if (input.hasReplyTo()) {
            message.setReplyTo(RealmRoomMessage.putOrUpdate(input.getReplyTo(), roomId));
        }
        message.setRoomMessageContact(RealmRoomMessageContact.build(input.getContact()));
        message.setStatusVersion(input.getStatusVersion());
        message.setUpdateTime(input.getUpdateTime() * DateUtils.SECOND_IN_MILLIS);
        message.setCreateTime(input.getCreateTime() * DateUtils.SECOND_IN_MILLIS);
        realm.close();

        return message;
    }

    public long getRoomId() {
        return roomId;
    }

    public void setRoomId(long roomId) {
        this.roomId = roomId;
    }

    public long getMessageId() {
        return messageId;
    }

    public void setMessageId(long messageId) {
        this.messageId = messageId;
    }

    public long getMessageVersion() {
        return messageVersion;
    }

    public void setMessageVersion(long messageVersion) {
        this.messageVersion = messageVersion;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getStatusVersion() {
        return statusVersion;
    }

    public void setStatusVersion(long statusVersion) {
        this.statusVersion = statusVersion;
    }

    public ProtoGlobal.RoomMessageType getMessageType() {
        return ProtoGlobal.RoomMessageType.valueOf(messageType);
    }

    public void setMessageType(ProtoGlobal.RoomMessageType messageType) {
        this.messageType = messageType.toString();
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public RealmRoomMessageLocation getLocation() {
        return location;
    }

    public void setLocation(RealmRoomMessageLocation location) {
        this.location = location;
    }

    public RealmRoomMessageLog getLog() {
        return log;
    }

    public void setLog(RealmRoomMessageLog log) {
        this.log = log;
    }

    public RealmRoomMessageContact getRoomMessageContact() {
        return roomMessageContact;
    }

    public void setRoomMessageContact(RealmRoomMessageContact roomMessageContact) {
        this.roomMessageContact = roomMessageContact;
    }

    public boolean isEdited() {
        return edited;
    }

    public void setEdited(boolean edited) {
        this.edited = edited;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public RealmRoomMessage getForwardMessage() {
        return forwardMessage;
    }

    public void setForwardMessage(RealmRoomMessage forwardMessage) {
        this.forwardMessage = forwardMessage;
    }

    public RealmRoomMessage getReplyTo() {
        return replyTo;
    }

    public void setReplyTo(RealmRoomMessage replyTo) {
        this.replyTo = replyTo;
    }

    public boolean isSenderMe() {
        Realm realm = Realm.getDefaultInstance();
        boolean output = getUserId() == realm.where(RealmUserInfo.class).findFirst().getUserId();
        realm.close();
        return output;
    }

    public boolean isOnlyTime() {
        return userId == -1;
    }

    public RealmAttachment getAttachment() {
        return attachment;
    }

    public void setAttachment(RealmAttachment attachment) {
        this.attachment = attachment;
    }

    public void setAttachment(final long messageId, final ProtoGlobal.File attachment) {
        Realm realm = Realm.getDefaultInstance();
        if (!attachment.getToken().isEmpty()) {
            if (this.attachment == null) {
                final RealmAttachment realmAttachment = realm.createObject(RealmAttachment.class, messageId);
                realmAttachment.setCacheId(attachment.getCacheId());
                realmAttachment.setDuration(attachment.getDuration());
                realmAttachment.setHeight(attachment.getHeight());
                realmAttachment.setName(attachment.getName());
                realmAttachment.setSize(attachment.getSize());
                realmAttachment.setToken(attachment.getToken());
                realmAttachment.setWidth(attachment.getWidth());

                long smallMessageThumbnail = SUID.id().get();
                RealmThumbnail.create(smallMessageThumbnail, messageId, attachment.getSmallThumbnail());

                long largeMessageThumbnail = SUID.id().get();
                RealmThumbnail.create(largeMessageThumbnail, messageId, attachment.getSmallThumbnail());

                realmAttachment.setSmallThumbnail(realm.where(RealmThumbnail.class).equalTo(RealmThumbnailFields.ID, smallMessageThumbnail).findFirst());
                realmAttachment.setLargeThumbnail(realm.where(RealmThumbnail.class).equalTo(RealmThumbnailFields.ID, largeMessageThumbnail).findFirst());

                this.attachment = realmAttachment;
            } else {
                if (this.attachment.isValid()) {
                    this.attachment.setCacheId(attachment.getCacheId());
                    this.attachment.setDuration(attachment.getDuration());
                    this.attachment.setHeight(attachment.getHeight());
                    this.attachment.setName(attachment.getName());
                    this.attachment.setSize(attachment.getSize());
                    this.attachment.setToken(attachment.getToken());
                    this.attachment.setWidth(attachment.getWidth());

                    long smallMessageThumbnail = SUID.id().get();
                    RealmThumbnail.create(smallMessageThumbnail, messageId, attachment.getSmallThumbnail());

                    long largeMessageThumbnail = SUID.id().get();
                    RealmThumbnail.create(largeMessageThumbnail, messageId, attachment.getSmallThumbnail());

                    this.attachment.setSmallThumbnail(realm.where(RealmThumbnail.class).equalTo(RealmThumbnailFields.ID, smallMessageThumbnail).findFirst());
                    this.attachment.setLargeThumbnail(realm.where(RealmThumbnail.class).equalTo(RealmThumbnailFields.ID, largeMessageThumbnail).findFirst());
                }
            }
            realm.close();
        }
    }

    public void setAttachment(final long messageId, final String path, int width, int height, long size, String name, double duration,
                              LocalFileType type) {
        if (path == null) {
            return;
        }
        Realm realm = Realm.getDefaultInstance();
        if (attachment == null) {
            RealmAttachment realmAttachment = realm.where(RealmAttachment.class).equalTo(RealmAttachmentFields.ID, messageId).findFirst();
            if (realmAttachment == null) {
                realmAttachment = realm.createObject(RealmAttachment.class, messageId);
            }
            if (type == LocalFileType.THUMBNAIL) {
                realmAttachment.setLocalThumbnailPath(path);
            } else {
                realmAttachment.setLocalFilePath(path);
            }
            realmAttachment.setWidth(width);
            realmAttachment.setSize(size);
            realmAttachment.setHeight(height);
            realmAttachment.setName(name);
            realmAttachment.setDuration(duration);
            attachment = realmAttachment;
        } else {
            if (attachment.isValid()) {
                if (type == LocalFileType.THUMBNAIL) {
                    attachment.setLocalThumbnailPath(path);
                } else {
                    attachment.setLocalFilePath(path);
                }
            }
        }
        realm.close();
    }
}
