package com.iGap.response;

import com.iGap.proto.ProtoUserTwoStepVerificationVerifyRecoveryEmail;

public class UserTwoStepVerificationVerifyRecoveryEmailResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public UserTwoStepVerificationVerifyRecoveryEmailResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();

        ProtoUserTwoStepVerificationVerifyRecoveryEmail.UserTwoStepVerificationVerifyRecoveryEmailResponse.Builder builder = (ProtoUserTwoStepVerificationVerifyRecoveryEmail.UserTwoStepVerificationVerifyRecoveryEmailResponse.Builder) message;
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


