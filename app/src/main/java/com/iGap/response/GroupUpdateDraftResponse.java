package com.iGap.response;

import com.iGap.proto.ProtoGroupUpdateDraft;
import com.iGap.realm.RealmRoom;

public class GroupUpdateDraftResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public GroupUpdateDraftResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);
        this.message = protoClass;
        this.identity = identity;
        this.actionId = actionId;
    }

    @Override
    public void handler() {
        super.handler();
        ProtoGroupUpdateDraft.GroupUpdateDraftResponse.Builder updateDraft =
                (ProtoGroupUpdateDraft.GroupUpdateDraftResponse.Builder) message;

       /*
        * if another account get UpdateDraftResponse set draft to RealmRoom
        */


        if (updateDraft.getResponse().getId().isEmpty()) {
            RealmRoom.convertAndSetDraft(updateDraft.getRoomId(),
                    updateDraft.getDraft().getMessage(), updateDraft.getDraft().getReplyTo());
        }
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


