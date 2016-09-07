package com.iGap.response;

import com.iGap.G;
import com.iGap.proto.ProtoUserProfileGender;

public class UserProfileGenderResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public UserProfileGenderResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }


    @Override
    public void handler() {
        ProtoUserProfileGender.UserProfileSetGenderResponse.Builder userProfileGenderResponse = ProtoUserProfileGender.UserProfileSetGenderResponse.newBuilder();
        G.onUserProfileGenderResponse.onUserProfileEmailResponse(userProfileGenderResponse.getGender(), userProfileGenderResponse.getResponse());
    }

    @Override
    public void timeOut() {
    }

    @Override
    public void error() {
    }
}


