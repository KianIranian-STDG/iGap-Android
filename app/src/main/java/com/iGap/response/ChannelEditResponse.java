package com.iGap.response;

import com.iGap.proto.ProtoChannelEdit;

public class ChannelEditResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public ChannelEditResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();

        ProtoChannelEdit.ChannelEditResponse.Builder builder = (ProtoChannelEdit.ChannelEditResponse.Builder) message;
        builder.getRoomId();
        builder.getName();
        builder.getDescription();
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


