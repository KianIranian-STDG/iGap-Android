package com.iGap.realm;

import io.realm.RealmObject;

public class RealmRoomForwardMessage extends RealmObject {

    private long roomId;
    private long messageId;

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
}
