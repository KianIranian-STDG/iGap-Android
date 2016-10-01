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
    private String message = "hello";
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

    public void setAttachment(long messageId, ProtoGlobal.File attachment) {
        if (!attachment.getToken().isEmpty()) {
            if (this.attachment == null) {
                Realm realm = Realm.getDefaultInstance();
                RealmMessageAttachment realmMessageAttachment = realm.createObject(RealmMessageAttachment.class);
                realmMessageAttachment.setMessageId(messageId);
                realmMessageAttachment.setCacheId(attachment.getCacheId());
                realmMessageAttachment.setDuration(attachment.getDuration());
                realmMessageAttachment.setHeight(attachment.getHeight());
                realmMessageAttachment.setName(attachment.getName());
                realmMessageAttachment.setSize(attachment.getSize());
                realmMessageAttachment.setToken(attachment.getToken());
                realmMessageAttachment.setWidth(attachment.getWidth());
                this.attachment = realmMessageAttachment;
                realm.close();
            } else {
                this.attachment.setCacheId(attachment.getCacheId());
                this.attachment.setDuration(attachment.getDuration());
                this.attachment.setHeight(attachment.getHeight());
                this.attachment.setName(attachment.getName());
                this.attachment.setSize(attachment.getSize());
                this.attachment.setToken(attachment.getToken());
                this.attachment.setWidth(attachment.getWidth());
            }
        }
    }

    public void setAttachmentForLocalPath(final long messageId, final String localPath) {
        Realm realm = Realm.getDefaultInstance();
        if (attachment == null) {
            RealmMessageAttachment realmMessageAttachment = realm.createObject(RealmMessageAttachment.class);
            realmMessageAttachment.setMessageId(messageId);
            realmMessageAttachment.setLocalPath(localPath);
            attachment = realmMessageAttachment;
        } else {
            attachment.setLocalPath(localPath);
        }
        realm.close();
    }

    public void setAttachmentForToken(final long messageId, final String token) {
        Realm realm = Realm.getDefaultInstance();
        if (attachment == null) {
            RealmMessageAttachment realmMessageAttachment = realm.createObject(RealmMessageAttachment.class);
            realmMessageAttachment.setMessageId(messageId);
            realmMessageAttachment.setToken(token);
            attachment = realmMessageAttachment;
        } else {
            attachment.setToken(token);
        }
        realm.close();
    }

}
