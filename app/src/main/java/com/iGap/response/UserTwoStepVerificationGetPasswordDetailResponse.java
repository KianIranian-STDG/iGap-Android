package com.iGap.response;

import com.iGap.proto.ProtoUserTwoStepVerificationGetPasswordDetail;

public class UserTwoStepVerificationGetPasswordDetailResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public UserTwoStepVerificationGetPasswordDetailResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();

        ProtoUserTwoStepVerificationGetPasswordDetail.UserTwoStepVerificationGetPasswordDetailResponse.Builder builder = (ProtoUserTwoStepVerificationGetPasswordDetail.UserTwoStepVerificationGetPasswordDetailResponse.Builder) message;
        builder.getQuestionOne();
        builder.getQuestionTwo();
        builder.getHint();
        builder.getHasConfirmedRecoveryEmail();
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


