package com.iGap.response;

import com.iGap.proto.ProtoChannelUpdateDraft;
import com.iGap.realm.RealmRoom;

public class ChannelUpdateDraftResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public ChannelUpdateDraftResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();

        final ProtoChannelUpdateDraft.ChannelUpdateDraftResponse.Builder builder = (ProtoChannelUpdateDraft.ChannelUpdateDraftResponse.Builder) message;

        if (builder.getResponse().getId().isEmpty()) {
            RealmRoom.convertAndSetDraft(builder.getRoomId(), builder.getDraft().getMessage(), builder.getDraft().getReplyTo());
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


