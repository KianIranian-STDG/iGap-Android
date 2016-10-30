package com.iGap.response;

import com.iGap.G;
import com.iGap.proto.ProtoUserProfileGender;

public class UserProfileSetGenderResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public UserProfileSetGenderResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override public void handler() {
        ProtoUserProfileGender.UserProfileSetGenderResponse.Builder userProfileGenderResponse =
            ProtoUserProfileGender.UserProfileSetGenderResponse.newBuilder();
        G.onUserProfileSetGenderResponse.onUserProfileEmailResponse(
            userProfileGenderResponse.getGender(), userProfileGenderResponse.getResponse());
    }

    @Override public void timeOut() {
    }

    @Override public void error() {
    }
}


