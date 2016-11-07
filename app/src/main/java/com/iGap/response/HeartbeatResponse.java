package com.iGap.response;

import com.iGap.request.RequestHeartbeat;

public class HeartbeatResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public HeartbeatResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);
        this.message = protoClass;
        this.identity = identity;
        this.actionId = actionId;
    }

    @Override
    public void handler() {
        new RequestHeartbeat().heartBeat();
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


