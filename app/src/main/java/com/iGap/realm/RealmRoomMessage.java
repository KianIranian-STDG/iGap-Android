package com.iGap.realm;

import android.text.format.DateUtils;

import com.iGap.proto.ProtoGlobal;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class RealmRoomMessage extends RealmObject {
    @PrimaryKey
    private long messageId;
    private long messageVersion;
    private String status;
    private long statusVersion;
    private String messageType;
    private String message;
    private long userId;
    private String location;
    private String log;
    private boolean edited;
    private long updateTime;
    private RealmMessageAttachment attachment;

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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }

    public boolean isEdited() {
        return edited;
    }

    public void setEdited(boolean edited) {
        this.edited = edited;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(int updateTime) {
        this.updateTime = updateTime * DateUtils.SECOND_IN_MILLIS;
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

    public RealmMessageAttachment getAttachment() {
        return attachment;
    }

    public void setAttachment(final long messageId, final ProtoGlobal.File attachment) {
        Realm realm = Realm.getDefaultInstance();
        if (!attachment.getToken().isEmpty()) {
            if (this.attachment == null) {
                final RealmMessageAttachment realmMessageAttachment = realm.createObject(RealmMessageAttachment.class);
                realmMessageAttachment.setMessageId(messageId);
                realmMessageAttachment.setCacheId(attachment.getCacheId());
                realmMessageAttachment.setDuration(attachment.getDuration());
                realmMessageAttachment.setHeight(attachment.getHeight());
                realmMessageAttachment.setName(attachment.getName());
                realmMessageAttachment.setSize(attachment.getSize());
                realmMessageAttachment.setToken(attachment.getToken());
                realmMessageAttachment.setWidth(attachment.getWidth());

                long smallMessageThumbnail = System.nanoTime();
                RealmMessageThumbnail.create(smallMessageThumbnail, messageId, attachment.getSmallThumbnail());

                long largeMessageThumbnail = System.nanoTime();
                RealmMessageThumbnail.create(largeMessageThumbnail, messageId, attachment.getSmallThumbnail());

                realmMessageAttachment.setSmallThumbnail(realm.where(RealmMessageThumbnail.class).equalTo("id", smallMessageThumbnail).findFirst());
                realmMessageAttachment.setLargeThumbnail(realm.where(RealmMessageThumbnail.class).equalTo("id", largeMessageThumbnail).findFirst());

                this.attachment = realmMessageAttachment;
            } else {
                this.attachment.setCacheId(attachment.getCacheId());
                this.attachment.setDuration(attachment.getDuration());
                this.attachment.setHeight(attachment.getHeight());
                this.attachment.setName(attachment.getName());
                this.attachment.setSize(attachment.getSize());
                this.attachment.setToken(attachment.getToken());
                this.attachment.setWidth(attachment.getWidth());

                long smallMessageThumbnail = System.nanoTime();
                RealmMessageThumbnail.create(smallMessageThumbnail, messageId, attachment.getSmallThumbnail());

                long largeMessageThumbnail = System.nanoTime();
                RealmMessageThumbnail.create(largeMessageThumbnail, messageId, attachment.getSmallThumbnail());

                this.attachment.setSmallThumbnail(realm.where(RealmMessageThumbnail.class).equalTo("id", smallMessageThumbnail).findFirst());
                this.attachment.setLargeThumbnail(realm.where(RealmMessageThumbnail.class).equalTo("id", largeMessageThumbnail).findFirst());
            }
            realm.close();
        }
    }

    public void setAttachmentForLocalThumbnailPath(final long messageId, final String localPath) {
        Realm realm = Realm.getDefaultInstance();
        if (attachment == null) {
            if (realm.where(RealmMessageAttachment.class).equalTo("messageId", messageId).count() <= 0) {
                RealmMessageAttachment realmMessageAttachment = realm.createObject(RealmMessageAttachment.class);
                realmMessageAttachment.setMessageId(messageId);
                realmMessageAttachment.setLocalThumbnailPath(localPath);
                attachment = realmMessageAttachment;
            } else {
                RealmMessageAttachment realmMessageAttachment = realm.where(RealmMessageAttachment.class).equalTo("messageId", messageId).findFirst();
                realmMessageAttachment.setLocalThumbnailPath(localPath);
                attachment = realmMessageAttachment;
            }
        } else {
            attachment.setLocalThumbnailPath(localPath);
        }
        realm.close();
    }
}
