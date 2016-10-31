package com.iGap.response;

import com.iGap.proto.ProtoChatGetDraft;

public class ChatGetDraftResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity; // here identity is roomId

    public ChatGetDraftResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);
        this.message = protoClass;
        this.identity = identity;
        this.actionId = actionId;
    }

    @Override public void handler() {

        ProtoChatGetDraft.ChatGetDraftResponse.Builder updateDraft =
            (ProtoChatGetDraft.ChatGetDraftResponse.Builder) message;

        updateDraft.getDraft();
    }

    @Override public void timeOut() {
        super.timeOut();
    }

    @Override public void error() {
        super.error();
    }
}


