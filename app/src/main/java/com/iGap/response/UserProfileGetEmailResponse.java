package com.iGap.response;

import com.iGap.proto.ProtoUserProfileGetEmail;

public class UserProfileGetEmailResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public UserProfileGetEmailResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.actionId = actionId;
        this.message = protoClass;
        this.identity = identity;
    }

    @Override public void handler() {
        ProtoUserProfileGetEmail.UserProfileGetEmailResponse.Builder builder =
            (ProtoUserProfileGetEmail.UserProfileGetEmailResponse.Builder) message;
        builder.getEmail();
    }

    @Override public void timeOut() {
    }

    @Override public void error() {
    }
}


