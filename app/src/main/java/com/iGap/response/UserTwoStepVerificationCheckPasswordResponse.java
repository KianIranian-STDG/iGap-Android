package com.iGap.response;

import com.iGap.proto.ProtoUserTwoStepVerificationCheckPassword;

public class UserTwoStepVerificationCheckPasswordResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public UserTwoStepVerificationCheckPasswordResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();

        ProtoUserTwoStepVerificationCheckPassword.UserTwoStepVerificationCheckPasswordResponse.Builder builder = (ProtoUserTwoStepVerificationCheckPassword.UserTwoStepVerificationCheckPasswordResponse.Builder) message;
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


