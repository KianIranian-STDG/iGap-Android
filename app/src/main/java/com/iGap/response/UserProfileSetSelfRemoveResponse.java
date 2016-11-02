package com.iGap.response;

import com.iGap.proto.ProtoUserProfileSetSelfRemove;

public class UserProfileSetSelfRemoveResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public UserProfileSetSelfRemoveResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override public void handler() {
        ProtoUserProfileSetSelfRemove.UserProfileSetSelfRemoveResponse.Builder builder =
            (ProtoUserProfileSetSelfRemove.UserProfileSetSelfRemoveResponse.Builder) message;

        builder.getSelfRemove();
    }

    @Override public void timeOut() {
        super.timeOut();
    }

    @Override public void error() {
        super.error();
    }
}


