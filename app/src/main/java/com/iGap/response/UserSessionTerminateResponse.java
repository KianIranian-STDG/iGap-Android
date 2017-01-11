package com.iGap.response;

import com.iGap.G;
import com.iGap.proto.ProtoUserSessionTerminate;

public class UserSessionTerminateResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public UserSessionTerminateResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();
        ProtoUserSessionTerminate.UserSessionTerminateResponse.Builder builder = (ProtoUserSessionTerminate.UserSessionTerminateResponse.Builder) message;
        G.onUserSessionTerminate.onUserSessionTerminate(Long.parseLong(identity));
    }

    @Override
    public void timeOut() {
        super.timeOut();
        G.onUserSessionTerminate.onTimeOut();
    }

    @Override
    public void error() {
        super.error();
        G.onUserSessionTerminate.onError();
    }
}


