package com.iGap.response;

import com.iGap.G;
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

    @Override
    public void handler() {
        super.handler();
        ProtoUserProfileGetEmail.UserProfileGetEmailResponse.Builder builder =
                (ProtoUserProfileGetEmail.UserProfileGetEmailResponse.Builder) message;
        builder.getEmail();

        if (G.onUserProfileGetEmail != null) {
            G.onUserProfileGetEmail.onUserProfileGetEmail(builder.getEmail());
        }

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


