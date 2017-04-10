package com.iGap.response;

import com.iGap.proto.ProtoUserTwoStepVerificationChangeRecoveryQuestion;

public class UserTwoStepVerificationChangeRecoveryQuestionResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public UserTwoStepVerificationChangeRecoveryQuestionResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();

        ProtoUserTwoStepVerificationChangeRecoveryQuestion.UserTwoStepVerificationChangeRecoveryQuestionResponse.Builder builder = (ProtoUserTwoStepVerificationChangeRecoveryQuestion.UserTwoStepVerificationChangeRecoveryQuestionResponse.Builder) message;
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


