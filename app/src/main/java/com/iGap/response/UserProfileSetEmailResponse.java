package com.iGap.response;

import com.iGap.G;
import com.iGap.proto.ProtoUserProfileEmail;

public class UserProfileSetEmailResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public UserProfileSetEmailResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override public void handler() {
        ProtoUserProfileEmail.UserProfileSetEmailResponse.Builder userProfileEmail =
            (ProtoUserProfileEmail.UserProfileSetEmailResponse.Builder) message;
        G.onUserProfileSetEmailResponse.onUserProfileEmailResponse(userProfileEmail.getEmail(),
            userProfileEmail.getResponse());
    }

    @Override public void timeOut() {
    }

    @Override public void error() {
    }
}


