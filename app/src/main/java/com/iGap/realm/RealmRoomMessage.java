package com.iGap.realm;

import android.text.format.DateUtils;
import com.iGap.module.enums.LocalFileType;
import com.iGap.proto.ProtoGlobal;
import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;

public class RealmRoomMessage extends RealmObject {
    @PrimaryKey private long messageId;
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

    @Index private long roomId;

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

    public void setStatusVersion(int statusVersion) {
        this.statusVersion = statusVersion;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
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

    public void setUpdateTime(int updateTime) {
        this.updateTime = updateTime * DateUtils.SECOND_IN_MILLIS;
    }

    public int getUpdateTimeAsSeconds() {
        return (int) (updateTime / DateUtils.SECOND_IN_MILLIS);
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
        try {
            return getUserId() == realm.where(RealmUserInfo.class).findFirst().getUserId();
        } finally {
            realm.close();
        }
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
                final RealmAttachment realmAttachment = realm.createObject(RealmAttachment.class);
                realmAttachment.setId(messageId);
                realmAttachment.setCacheId(attachment.getCacheId());
                realmAttachment.setDuration(attachment.getDuration());
                realmAttachment.setHeight(attachment.getHeight());
                realmAttachment.setName(attachment.getName());
                realmAttachment.setSize(attachment.getSize());
                realmAttachment.setToken(attachment.getToken());
                realmAttachment.setWidth(attachment.getWidth());

                long smallMessageThumbnail = System.nanoTime();
                RealmThumbnail.create(smallMessageThumbnail, messageId,
                    attachment.getSmallThumbnail());

                long largeMessageThumbnail = System.nanoTime();
                RealmThumbnail.create(largeMessageThumbnail, messageId,
                    attachment.getSmallThumbnail());

                realmAttachment.setSmallThumbnail(realm.where(RealmThumbnail.class)
                    .equalTo("id", smallMessageThumbnail)
                    .findFirst());
                realmAttachment.setLargeThumbnail(realm.where(RealmThumbnail.class)
                    .equalTo("id", largeMessageThumbnail)
                    .findFirst());

                this.attachment = realmAttachment;
            } else {
                this.attachment.setCacheId(attachment.getCacheId());
                this.attachment.setDuration(attachment.getDuration());
                this.attachment.setHeight(attachment.getHeight());
                this.attachment.setName(attachment.getName());
                this.attachment.setSize(attachment.getSize());
                this.attachment.setToken(attachment.getToken());
                this.attachment.setWidth(attachment.getWidth());

                long smallMessageThumbnail = System.nanoTime();
                RealmThumbnail.create(smallMessageThumbnail, messageId,
                    attachment.getSmallThumbnail());

                long largeMessageThumbnail = System.nanoTime();
                RealmThumbnail.create(largeMessageThumbnail, messageId,
                    attachment.getSmallThumbnail());

                this.attachment.setSmallThumbnail(realm.where(RealmThumbnail.class)
                    .equalTo("id", smallMessageThumbnail)
                    .findFirst());
                this.attachment.setLargeThumbnail(realm.where(RealmThumbnail.class)
                    .equalTo("id", largeMessageThumbnail)
                    .findFirst());
            }
            realm.close();
        }
    }

    public void setAttachment(final long messageId, final String path, int width, int height,
        long size, String name, double duration, LocalFileType type) {
        if (path == null) {
            return;
        }
        Realm realm = Realm.getDefaultInstance();
        if (attachment == null) {
            RealmAttachment realmAttachment = realm.where(RealmAttachment.class)
                .equalTo(RealmAttachmentFields.ID, messageId)
                .findFirst();
            if (realmAttachment == null) {
                realmAttachment = realm.createObject(RealmAttachment.class);
                realmAttachment.setId(messageId);
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
            if (type == LocalFileType.THUMBNAIL) {
                attachment.setLocalThumbnailPath(path);
            } else {
                attachment.setLocalFilePath(path);
            }
        }
        realm.close();
    }
}
