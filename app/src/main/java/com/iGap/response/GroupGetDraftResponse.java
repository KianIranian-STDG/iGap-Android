package com.iGap.response;

import com.iGap.proto.ProtoGroupGetDraft;

public class GroupGetDraftResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity; // here identity is roomId

    public GroupGetDraftResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);
        this.message = protoClass;
        this.identity = identity;
        this.actionId = actionId;
    }

    @Override public void handler() {

        ProtoGroupGetDraft.GroupGetDraftResponse.Builder updateDraft =
            (ProtoGroupGetDraft.GroupGetDraftResponse.Builder) message;

        updateDraft.getDraft();
    }

    @Override public void timeOut() {
        super.timeOut();
    }

    @Override public void error() {
        super.error();
    }
}


