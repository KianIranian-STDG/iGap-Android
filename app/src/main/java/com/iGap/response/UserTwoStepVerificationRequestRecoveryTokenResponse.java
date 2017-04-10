package com.iGap.response;

import com.iGap.proto.ProtoUserTwoStepVerificationRequestRecoveryToken;

public class UserTwoStepVerificationRequestRecoveryTokenResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public UserTwoStepVerificationRequestRecoveryTokenResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();

        ProtoUserTwoStepVerificationRequestRecoveryToken.UserTwoStepVerificationRequestRecoveryTokenResponse.Builder builder = (ProtoUserTwoStepVerificationRequestRecoveryToken.UserTwoStepVerificationRequestRecoveryTokenResponse.Builder) message;
        builder.getEmailPattern();
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


