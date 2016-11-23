package com.iGap.response;

import com.iGap.proto.ProtoChannelAddAdmin;

public class ChannelAddAdminResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public ChannelAddAdminResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();

        ProtoChannelAddAdmin.ChannelAddAdminResponse.Builder builder = (ProtoChannelAddAdmin.ChannelAddAdminResponse.Builder) message;
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


