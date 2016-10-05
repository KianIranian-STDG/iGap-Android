package com.iGap.response;

import com.iGap.proto.ProtoUserAvatarAdd;

public class UserAvatarAddResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public UserAvatarAddResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);
        this.message = protoClass;
        this.identity = identity;
        this.actionId = actionId;
    }

    @Override
    public void handler() {
        super.handler();

        ProtoUserAvatarAdd.UserAvatarAddResponse.Builder userAvatarAddResponse = (ProtoUserAvatarAdd.UserAvatarAddResponse.Builder) message;
        userAvatarAddResponse.getId();
        userAvatarAddResponse.getAttachment();
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


