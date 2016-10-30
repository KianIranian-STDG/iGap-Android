package com.iGap.response;

import android.util.Log;

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

    @Override public void handler() {
        Log.i("CLI", "handler : " + message);
    }

    @Override public void timeOut() {
        Log.i("CLI", "timeOut : " + message);
    }

    @Override public void error() {
        Log.i("CLI", "error : " + message);
    }
}


