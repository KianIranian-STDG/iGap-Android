package com.iGap.response;

import com.iGap.proto.ProtoGroupAvatarGetList;

public class GroupAvatarGetListResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public GroupAvatarGetListResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);
        this.message = protoClass;
        this.identity = identity;
        this.actionId = actionId;
    }

    @Override
    public void handler() {
        super.handler();

        ProtoGroupAvatarGetList.GroupAvatarGetListResponse.Builder userAvatarGetListResponse =
                (ProtoGroupAvatarGetList.GroupAvatarGetListResponse.Builder) message;
        userAvatarGetListResponse.getAvatarList();
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


