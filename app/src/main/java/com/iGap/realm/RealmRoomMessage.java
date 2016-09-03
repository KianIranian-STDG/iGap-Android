package com.iGap.realm;

import io.realm.RealmObject;

public class RealmRoomMessage extends RealmObject {

//    @PrimaryKey
//    private long id;

    private long messageId;
    private int messageVersion;
    private String status;
    private int statusVersion;
    private String messageType;
    private String message;
    private String attachment;
    private long userId;
    private String location;
    private String log;
    private boolean edited;
    private int updateTime;

    public long getMessageId() {
        return messageId;
    }

    public void setMessageId(long messageId) {
        this.messageId = messageId;
    }

    public int getMessageVersion() {
        return messageVersion;
    }

    public void setMessageVersion(int messageVersion) {
        this.messageVersion = messageVersion;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getStatusVersion() {
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

    public String getAttachment() {
        return attachment;
    }

    public void setAttachment(String attachment) {
        this.attachment = attachment;
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

    public int getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(int updateTime) {
        this.updateTime = updateTime;
    }
}
