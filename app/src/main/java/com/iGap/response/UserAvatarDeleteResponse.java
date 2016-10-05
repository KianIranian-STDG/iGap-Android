package com.iGap.response;

import com.iGap.proto.ProtoUserAvatarDelete;

public class UserAvatarDeleteResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public UserAvatarDeleteResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);
        this.message = protoClass;
        this.identity = identity;
        this.actionId = actionId;
    }

    @Override
    public void handler() {
        super.handler();

        ProtoUserAvatarDelete.UserAvatarDeleteResponse.Builder userAvatarDeleteResponse = (ProtoUserAvatarDelete.UserAvatarDeleteResponse.Builder) message;
        userAvatarDeleteResponse.getId();
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


