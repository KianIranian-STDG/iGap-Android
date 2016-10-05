package com.iGap.response;

import com.iGap.proto.ProtoUserAvatarGetList;

public class UserAvatarGetListResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public UserAvatarGetListResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);
        this.message = protoClass;
        this.identity = identity;
        this.actionId = actionId;
    }

    @Override
    public void handler() {
        super.handler();

        ProtoUserAvatarGetList.UserAvatarGetListResponse.Builder userAvatarGetListResponse = (ProtoUserAvatarGetList.UserAvatarGetListResponse.Builder) message;
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


