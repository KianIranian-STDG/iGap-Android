package com.iGap.response;

import com.iGap.proto.ProtoChannelGetDraft;

public class ChannelGetDraftResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public ChannelGetDraftResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();

        ProtoChannelGetDraft.ChannelGetDraftResponse.Builder builder = (ProtoChannelGetDraft.ChannelGetDraftResponse.Builder) message;
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


