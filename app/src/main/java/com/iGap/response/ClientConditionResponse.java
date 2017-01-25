package com.iGap.response;

import com.iGap.G;

public class ClientConditionResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public ClientConditionResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();
        if (G.onClientCondition != null) {
            G.onClientCondition.onClientCondition();
        }
    }

    @Override
    public void timeOut() {
        super.timeOut();
        /**
         * timeOut call error also
         */
    }

    @Override
    public void error() {
        super.error();
        if (G.onClientCondition != null) {
            G.onClientCondition.onClientConditionError();
        }
    }
}


