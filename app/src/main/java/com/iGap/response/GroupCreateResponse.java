package com.iGap.response;

import com.iGap.proto.ProtoGroupCreate;

public class GroupCreateResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public GroupCreateResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }


    @Override
    public void handler() {

        ProtoGroupCreate.GroupCreateResponse.Builder builder = (ProtoGroupCreate.GroupCreateResponse.Builder) message;
        builder.getRoomId();

    }

    @Override
    public void error() {

    }
}
