package com.iGap.response;

import com.iGap.proto.ProtoGroupSetAction;

public class GroupSetActionResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public GroupSetActionResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        ProtoGroupSetAction.GroupSetActionResponse.Builder builder = (ProtoGroupSetAction.GroupSetActionResponse.Builder) message;
        builder.getUserId();
        builder.getRoomId();
        builder.getAction();
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


