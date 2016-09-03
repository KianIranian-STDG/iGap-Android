package com.iGap.realm;

import io.realm.RealmObject;

public class RealmChatHistory extends RealmObject {

//    @PrimaryKey private long id;

    private long roomId;
    private RealmRoomMessage roomMessage;

    public RealmRoomMessage getRoomMessage() {
        return roomMessage;
    }

    public void setRoomMessage(RealmRoomMessage roomMessage) {
        this.roomMessage = roomMessage;
    }

    public long getRoomId() {
        return roomId;
    }

    public void setRoomId(long roomId) {
        this.roomId = roomId;
    }


}
