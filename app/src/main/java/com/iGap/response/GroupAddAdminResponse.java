package com.iGap.response;

import com.iGap.proto.ProtoGroupAddAdmin;

public class GroupAddAdminResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public GroupAddAdminResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }


    @Override
    public void handler() {

        ProtoGroupAddAdmin.GroupAddAdminResponse.Builder builder = (ProtoGroupAddAdmin.GroupAddAdminResponse.Builder) message;
        builder.getRoomId();
        builder.getMemberId();

    }

    @Override
    public void error() {

    }
}
