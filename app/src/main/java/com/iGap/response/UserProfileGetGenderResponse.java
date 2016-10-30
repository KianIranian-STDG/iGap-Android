package com.iGap.response;

import com.iGap.proto.ProtoUserProfileGetGender;

public class UserProfileGetGenderResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public UserProfileGetGenderResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.actionId = actionId;
        this.message = protoClass;
        this.identity = identity;
    }

    @Override public void handler() {
        ProtoUserProfileGetGender.UserProfileGetGenderResponse.Builder builder =
            (ProtoUserProfileGetGender.UserProfileGetGenderResponse.Builder) message;
        builder.getGender();
    }

    @Override public void timeOut() {
    }

    @Override public void error() {
    }
}


