package com.iGap.response;

import com.iGap.proto.ProtoUserTwoStepVerificationChangeHint;

public class UserTwoStepVerificationChangeHintResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public UserTwoStepVerificationChangeHintResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();

        ProtoUserTwoStepVerificationChangeHint.UserTwoStepVerificationChangeHintResponse.Builder builder = (ProtoUserTwoStepVerificationChangeHint.UserTwoStepVerificationChangeHintResponse.Builder) message;
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


