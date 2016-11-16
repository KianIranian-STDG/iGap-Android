package com.iGap.response;

import com.iGap.G;

public class UserSessionLogoutResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public UserSessionLogoutResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();
        G.onUserSessionLogout.onUserSessionLogout();
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


