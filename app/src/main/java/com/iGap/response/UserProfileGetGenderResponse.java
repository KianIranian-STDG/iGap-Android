package com.iGap.response;

import com.iGap.G;
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

    @Override
    public void handler() {
        super.handler();
        final ProtoUserProfileGetGender.UserProfileGetGenderResponse.Builder builder =
                (ProtoUserProfileGetGender.UserProfileGetGenderResponse.Builder) message;

        if (G.onUserProfileGetGender != null) {
            G.onUserProfileGetGender.onUserProfileGetGender(builder.getGender());
        }

    }

    @Override
    public void timeOut() {
        super.timeOut();
        if (G.onUserProfileGetGender != null) {
            G.onUserProfileGetGender.onUserProfileGetGenderTimeOut();
        }

    }

    @Override
    public void error() {
        super.error();
        if (G.onUserProfileGetGender != null) {
            G.onUserProfileGetGender.onUserProfileGetGenderError();
        }

    }
}


