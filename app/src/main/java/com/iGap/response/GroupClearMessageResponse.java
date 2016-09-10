package com.iGap.response;

import com.iGap.proto.ProtoGroupClearMessage;

public class GroupClearMessageResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public GroupClearMessageResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }


    @Override
    public void handler() {

        ProtoGroupClearMessage.GroupClearMessageResponse.Builder builder = (ProtoGroupClearMessage.GroupClearMessageResponse.Builder) message;
        builder.getRoomId();
        builder.getClearId();

    }

    @Override
    public void error() {

    }
}
