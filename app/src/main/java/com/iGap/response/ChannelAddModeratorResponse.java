package com.iGap.response;

import com.iGap.proto.ProtoChannelAddModerator;

public class ChannelAddModeratorResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public ChannelAddModeratorResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();

        ProtoChannelAddModerator.ChannelAddModeratorResponse.Builder builder = (ProtoChannelAddModerator.ChannelAddModeratorResponse.Builder) message;
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


