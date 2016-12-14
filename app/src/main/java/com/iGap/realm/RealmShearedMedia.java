package com.iGap.realm;

import io.realm.RealmObject;

/**
 * Created by android3 on 12/5/2016.
 */


public class RealmShearedMedia extends RealmObject {

    private long messageId;

    private long roomId;

    private byte[] roomMessage;

    private byte[] filter;


    public byte[] getRoomMessage() {
        return roomMessage;
    }

    public void setRoomMessage(byte[] roomMessage) {
        this.roomMessage = roomMessage;
    }

    public byte[] getFilter() {
        return filter;
    }

    public void setFilter(byte[] filter) {
        this.filter = filter;
    }

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

}
