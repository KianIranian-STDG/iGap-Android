package com.iGap.response;

import com.iGap.proto.ProtoGroupAvatarDelete;

public class GroupAvatarDeleteResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public GroupAvatarDeleteResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);
        this.message = protoClass;
        this.identity = identity;
        this.actionId = actionId;
    }

    @Override
    public void handler() {
        super.handler();

        ProtoGroupAvatarDelete.GroupAvatarDelete.Builder groupAvatarDelete =
                (ProtoGroupAvatarDelete.GroupAvatarDelete.Builder) message;
        groupAvatarDelete.getRoomId();
        groupAvatarDelete.getId();
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


