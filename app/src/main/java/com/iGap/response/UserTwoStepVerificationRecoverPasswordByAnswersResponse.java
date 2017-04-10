package com.iGap.response;

import com.iGap.proto.ProtoUserTwoStepVerificationRecoverPasswordByAnswers;

public class UserTwoStepVerificationRecoverPasswordByAnswersResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public UserTwoStepVerificationRecoverPasswordByAnswersResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();

        ProtoUserTwoStepVerificationRecoverPasswordByAnswers.UserTwoStepVerificationRecoverPasswordByAnswersResponse.Builder builder = (ProtoUserTwoStepVerificationRecoverPasswordByAnswers.UserTwoStepVerificationRecoverPasswordByAnswersResponse.Builder) message;
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


