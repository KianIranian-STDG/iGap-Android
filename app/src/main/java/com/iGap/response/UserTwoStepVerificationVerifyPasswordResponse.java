package com.iGap.response;

import com.iGap.proto.ProtoUserTwoStepVerificationVerifyPassword;

public class UserTwoStepVerificationVerifyPasswordResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public UserTwoStepVerificationVerifyPasswordResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();

        ProtoUserTwoStepVerificationVerifyPassword.UserTwoStepVerificationVerifyPasswordResponse.Builder builder = (ProtoUserTwoStepVerificationVerifyPassword.UserTwoStepVerificationVerifyPasswordResponse.Builder) message;
        builder.getToken();
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


