package com.iGap.response;

import com.iGap.proto.ProtoUserTwoStepVerificationRecoverPasswordByToken;

public class UserTwoStepVerificationRecoverPasswordByTokenResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public UserTwoStepVerificationRecoverPasswordByTokenResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();

        ProtoUserTwoStepVerificationRecoverPasswordByToken.UserTwoStepVerificationRecoverPasswordByTokenResponse.Builder builder = (ProtoUserTwoStepVerificationRecoverPasswordByToken.UserTwoStepVerificationRecoverPasswordByTokenResponse.Builder) message;
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


