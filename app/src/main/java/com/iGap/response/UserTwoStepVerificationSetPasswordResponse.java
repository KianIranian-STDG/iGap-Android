package com.iGap.response;

import com.iGap.proto.ProtoUserTwoStepVerificationSetPassword;

public class UserTwoStepVerificationSetPasswordResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public UserTwoStepVerificationSetPasswordResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();

        ProtoUserTwoStepVerificationSetPassword.UserTwoStepVerificationSetPasswordResponse.Builder builder = (ProtoUserTwoStepVerificationSetPassword.UserTwoStepVerificationSetPasswordResponse.Builder) message;
        builder.getUnconfirmedEmailPattern();
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


