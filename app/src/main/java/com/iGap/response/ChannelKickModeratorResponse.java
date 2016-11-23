package com.iGap.response;

import com.iGap.proto.ProtoChannelKickModerator;

public class ChannelKickModeratorResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public ChannelKickModeratorResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();

        ProtoChannelKickModerator.ChannelKickModeratorResponse.Builder builder = (ProtoChannelKickModerator.ChannelKickModeratorResponse.Builder) message;
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


