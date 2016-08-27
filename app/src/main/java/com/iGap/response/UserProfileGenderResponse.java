package com.iGap.response;

import com.iGap.G;
import com.iGap.proto.ProtoUserProfileGender;

public class UserProfileGenderResponse extends MessageHandler {

    public int actionId;
    public Object message;

    public UserProfileGenderResponse(int actionId, Object protoClass) {
        super(actionId, protoClass);

        this.message = protoClass;
        this.actionId = actionId;
    }


    @Override
    public void handler() {
        ProtoUserProfileGender.UserProfileGenderResponse.Builder userProfileGenderResponse = ProtoUserProfileGender.UserProfileGenderResponse.newBuilder();
        G.onUserProfileGenderResponse.onUserProfileEmailResponse(userProfileGenderResponse.getGender(), userProfileGenderResponse.getResponse());
    }

    @Override
    public void timeOut() {
    }

    @Override
    public void error() {
    }
}


