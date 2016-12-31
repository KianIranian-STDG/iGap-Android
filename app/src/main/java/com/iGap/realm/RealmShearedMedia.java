package com.iGap.realm;

import io.realm.RealmObject;

/**
 * Created by android3 on 12/5/2016.
 */


public class RealmShearedMedia extends RealmObject {

    private long messageId;
    private long roomId;
    private int messageType;
    private boolean isItemTime = false;
    private long messageTime;
    private String attachment_token;
    private String attachment_name;
    private long attachment_size;

    private long attachment_smallThumbnail_size;

    private double attachment_duration;
    private byte[] roomMessage;

    public long getMessageId() {
        return messageId;
    }

    public void setMessageId(long messageId) {
        this.messageId = messageId;
    }

    public long getRoomId() {
        return roomId;
    }

    public void setRoomId(long roomId) {
        this.roomId = roomId;
    }

    public int getMessageType() {
        return messageType;
    }

    public void setMessageType(int messageType) {
        this.messageType = messageType;
    }

    public boolean isItemTime() {
        return isItemTime;
    }

    public void setItemTime(boolean itemTime) {
        isItemTime = itemTime;
    }

    public long getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(long messageTime) {
        this.messageTime = messageTime;
    }

    public String getAttachment_token() {
        return attachment_token;
    }

    public void setAttachment_token(String attachment_token) {
        this.attachment_token = attachment_token;
    }

    public String getAttachment_name() {
        return attachment_name;
    }

    public void setAttachment_name(String attachment_name) {
        this.attachment_name = attachment_name;
    }

    public long getAttachment_size() {
        return attachment_size;
    }

    public void setAttachment_size(long attachment_size) {
        this.attachment_size = attachment_size;
    }

    public long getAttachment_smallThumbnail_size() {
        return attachment_smallThumbnail_size;
    }

    public void setAttachment_smallThumbnail_size(long attachment_smallThumbnail_size) {
        this.attachment_smallThumbnail_size = attachment_smallThumbnail_size;
    }

    public double getAttachment_duration() {
        return attachment_duration;
    }

    public void setAttachment_duration(double attachment_duration) {
        this.attachment_duration = attachment_duration;
    }

    public byte[] getRoomMessage() {
        return roomMessage;
    }

    public void setRoomMessage(byte[] roomMessage) {
        this.roomMessage = roomMessage;
    }
}
