package com.iGap.response;

import com.iGap.proto.ProtoGroupLeft;

public class GroupLeftResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public GroupLeftResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }


    @Override
    public void handler() {

        ProtoGroupLeft.GroupLeftResponse.Builder builder = (ProtoGroupLeft.GroupLeftResponse.Builder) message;
        builder.getRoomId();
        builder.getMemberId();

    }

    @Override
    public void error() {

    }
}
