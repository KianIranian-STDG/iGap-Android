package com.iGap.response;

import com.iGap.proto.ProtoUserTwoStepVerificationChangeRecoveryEmail;

public class UserTwoStepVerificationChangeRecoveryEmailResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public UserTwoStepVerificationChangeRecoveryEmailResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();

        ProtoUserTwoStepVerificationChangeRecoveryEmail.UserTwoStepVerificationChangeRecoveryEmailResponse.Builder builder = (ProtoUserTwoStepVerificationChangeRecoveryEmail.UserTwoStepVerificationChangeRecoveryEmailResponse.Builder) message;
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


