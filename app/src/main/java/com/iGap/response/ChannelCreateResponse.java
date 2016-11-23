package com.iGap.response;

import com.iGap.proto.ProtoChannelCreate;

public class ChannelCreateResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public ChannelCreateResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();
        ProtoChannelCreate.ChannelCreateResponse.Builder builder = (ProtoChannelCreate.ChannelCreateResponse.Builder) message;
        builder.getRoomId();
        builder.getInviteLink();
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


