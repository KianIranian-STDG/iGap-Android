package com.iGap.response;

import com.iGap.G;

public class UserDeleteResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public UserDeleteResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override public void handler() {
        G.onUserDelete.onUserDeleteResponse();
    }

    @Override public void timeOut() {
        super.timeOut();
    }

    @Override public void error() {
        super.error();
    }
}


