package com.iGap.response;

import com.iGap.proto.ProtoChannelLeft;

public class ChannelLeftResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public ChannelLeftResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();

        ProtoChannelLeft.ChannelLeftResponse.Builder builder = (ProtoChannelLeft.ChannelLeftResponse.Builder) message;
        builder.getRoomId();
        builder.getMemberId();
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


