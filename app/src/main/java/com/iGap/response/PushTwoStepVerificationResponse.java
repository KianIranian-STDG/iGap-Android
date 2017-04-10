package com.iGap.response;

import com.iGap.proto.ProtoPushTwoStepVerification;

public class PushTwoStepVerificationResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public PushTwoStepVerificationResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();

        ProtoPushTwoStepVerification.PushTwoStepVerificationResponse.Builder builder = (ProtoPushTwoStepVerification.PushTwoStepVerificationResponse.Builder) message;
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


