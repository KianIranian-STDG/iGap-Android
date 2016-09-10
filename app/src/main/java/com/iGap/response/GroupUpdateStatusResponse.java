package com.iGap.response;

import com.iGap.proto.ProtoGroupUpdateStatus;

public class GroupUpdateStatusResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public GroupUpdateStatusResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }


    @Override
    public void handler() {

        ProtoGroupUpdateStatus.GroupUpdateStatusResponse.Builder builder = (ProtoGroupUpdateStatus.GroupUpdateStatusResponse.Builder) message;
        builder.getRoomId();
        builder.getMessageId();
        builder.getStatus();
        builder.getStatusVersion();

    }

    @Override
    public void error() {

    }
}
