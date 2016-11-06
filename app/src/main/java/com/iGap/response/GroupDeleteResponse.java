package com.iGap.response;

import com.iGap.G;
import com.iGap.proto.ProtoGroupDelete;

public class GroupDeleteResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public GroupDeleteResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override public void handler() {
        ProtoGroupDelete.GroupDeleteResponse.Builder builder = (ProtoGroupDelete.GroupDeleteResponse.Builder) message;
        G.onGroupDelete.onGroupDelete(builder.getRoomId());
    }

    @Override public void timeOut() {
        super.timeOut();
    }

    @Override public void error() {
        super.error();
    }
}


