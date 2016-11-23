package com.iGap.response;

import com.iGap.proto.ProtoChannelUpdateDraft;

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

        ProtoChannelUpdateDraft.ChannelUpdateDraftResponse.Builder builder = (ProtoChannelUpdateDraft.ChannelUpdateDraftResponse.Builder) message;
        builder.getRoomId();
        builder.getDraft();
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


