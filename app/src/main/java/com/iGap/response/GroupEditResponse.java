package com.iGap.response;

import com.iGap.G;
import com.iGap.proto.ProtoGroupEdit;

public class GroupEditResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public GroupEditResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }


    @Override
    public void handler() {

        ProtoGroupEdit.GroupEditResponse.Builder builder = (ProtoGroupEdit.GroupEditResponse.Builder) message;
        builder.getRoomId();
        builder.getName();
        builder.getDescription();

        G.onGroupEdit.onGroupEdit(builder.getRoomId(), builder.getName(), builder.getDescription());



    }

    @Override
    public void error() {

    }
}
