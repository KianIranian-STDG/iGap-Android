package com.iGap.response;

import com.iGap.proto.ProtoGroupSendMessage;

public class GroupSendMessageResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public GroupSendMessageResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }


    @Override
    public void handler() {

        ProtoGroupSendMessage.GroupSendMessageResponse.Builder builder = (ProtoGroupSendMessage.GroupSendMessageResponse.Builder) message;
        builder.getRoomId();
        builder.getRoomMessage();

    }

    @Override
    public void error() {

    }
}
