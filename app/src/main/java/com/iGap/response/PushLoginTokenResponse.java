package com.iGap.response;

import com.iGap.proto.ProtoPushLoginToken;

public class PushLoginTokenResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public PushLoginTokenResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();

        ProtoPushLoginToken.PushLoginTokenResponse.Builder builder = (ProtoPushLoginToken.PushLoginTokenResponse.Builder) message;
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


