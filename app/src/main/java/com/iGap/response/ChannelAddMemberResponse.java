package com.iGap.response;

import com.iGap.proto.ProtoChannelAddMember;

public class ChannelAddMemberResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public ChannelAddMemberResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();

        ProtoChannelAddMember.ChannelAddMemberResponse.Builder builder = (ProtoChannelAddMember.ChannelAddMemberResponse.Builder) message;
        builder.getRoomId();
        builder.getUserId();
        builder.getRole();
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


