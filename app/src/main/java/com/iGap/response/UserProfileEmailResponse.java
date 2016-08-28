package com.iGap.response;

import com.iGap.G;
import com.iGap.proto.ProtoUserProfileEmail;

public class UserProfileEmailResponse extends MessageHandler {

    public int actionId;
    public Object message;

    public UserProfileEmailResponse(int actionId, Object protoClass) {
        super(actionId, protoClass);

        this.message = protoClass;
        this.actionId = actionId;
    }


    @Override
    public void handler() {

        ProtoUserProfileEmail.UserProfileEmailResponse.Builder userProfileEmail = (ProtoUserProfileEmail.UserProfileEmailResponse.Builder) message;
        G.onUserProfileEmailResponse.onUserProfileEmailResponse(userProfileEmail.getEmail(), userProfileEmail.getResponse());
    }

    @Override
    public void timeOut() {
    }

    @Override
    public void error() {
    }
}


