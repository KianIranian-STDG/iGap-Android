package com.iGap.response;

import com.iGap.proto.ProtoGroupAddModerator;

public class GroupAddModeratorResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public GroupAddModeratorResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }


    @Override
    public void handler() {

        ProtoGroupAddModerator.GroupAddModeratorResponse.Builder builder = (ProtoGroupAddModerator.GroupAddModeratorResponse.Builder) message;
        builder.getRoomId();
        builder.getMemberId();

    }

    @Override
    public void error() {

    }
}
