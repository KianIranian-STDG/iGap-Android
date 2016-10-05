package com.iGap.response;

import com.iGap.proto.ProtoGroupAvatarAdd;

public class GroupAvatarAddResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public GroupAvatarAddResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);
        this.message = protoClass;
        this.identity = identity;
        this.actionId = actionId;
    }

    @Override
    public void handler() {
        super.handler();

        ProtoGroupAvatarAdd.GroupAvatarAddResponse.Builder groupAvatarAddResponse = (ProtoGroupAvatarAdd.GroupAvatarAddResponse.Builder) message;
        groupAvatarAddResponse.getAttachment();
        groupAvatarAddResponse.getRoomId();
        groupAvatarAddResponse.getId();
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


