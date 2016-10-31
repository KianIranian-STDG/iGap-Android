package com.iGap.realm;

import io.realm.RealmObject;

public class RealmRoomDraft extends RealmObject {

    private String message;
    private long replyToMessageId;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getReplyToMessageId() {
        return replyToMessageId;
    }

    public void setReplyToMessageId(long replyToMessageId) {
        this.replyToMessageId = replyToMessageId;
    }
}
