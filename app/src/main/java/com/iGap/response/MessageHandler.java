package com.iGap.response;

import android.util.Log;

public abstract class MessageHandler {

    public Object message;
    int actionId;
    String identity;

    public MessageHandler(int actionId, Object protoClass, String identity) {

        this.actionId = actionId;
        this.message = protoClass;
        this.identity = identity;
    }

    public void handler() throws NullPointerException {
        Log.i("MSGH", "MessageHandler handler : " + message);
    }

    public void timeOut() {
        Log.i("MSGT", "MessageHandler timeOut : " + message);
        error();
    }

    public void error() {
        Log.i("MSGE", "MessageHandler error : " + message);
    }
}
