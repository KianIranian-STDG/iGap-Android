package com.iGap.response;

import com.iGap.proto.ProtoGroupGetDraft;
import com.iGap.realm.RealmRoom;

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

    @Override
    public void handler() {
        super.handler();
        ProtoGroupGetDraft.GroupGetDraftResponse.Builder groupGetDraft =
                (ProtoGroupGetDraft.GroupGetDraftResponse.Builder) message;

        RealmRoom.convertAndSetDraft(Long.parseLong(identity),
                groupGetDraft.getDraft().getMessage(), groupGetDraft.getDraft().getReplyTo());
    }

    @Override
    public void timeOut() {
        super.timeOut();
    }

    @Override
    public void error() {
        super.error();
    }
}


