package com.iGap.response;

import com.iGap.G;
import com.iGap.proto.ProtoGroupGetMemberList;

public class GroupGetMemberListResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public GroupGetMemberListResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override public void handler() {

        ProtoGroupGetMemberList.GroupGetMemberListResponse.Builder builder =
            (ProtoGroupGetMemberList.GroupGetMemberListResponse.Builder) message;

        G.onGroupGetMemberList.onGroupGetMemberList(builder.getMemberList());
    }

    @Override public void timeOut() {
        super.timeOut();
    }

    @Override public void error() {
        super.error();
    }
}


