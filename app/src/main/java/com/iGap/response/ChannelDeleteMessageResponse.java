package com.iGap.response;

import com.iGap.proto.ProtoChannelDeleteMessage;

public class ChannelDeleteMessageResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public ChannelDeleteMessageResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();

        ProtoChannelDeleteMessage.ChannelDeleteMessageResponse.Builder builder = (ProtoChannelDeleteMessage.ChannelDeleteMessageResponse.Builder) message;
        builder.getRoomId();
        builder.getMessageId();
        builder.getDeleteVersion();
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


