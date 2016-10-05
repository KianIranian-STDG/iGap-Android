package com.iGap.response;

import android.util.Log;

public abstract class MessageHandler {

    int actionId;
    public Object message;
    String identity;

    public MessageHandler(int actionId, Object protoClass, String identity) {

        this.actionId = actionId;
        this.message = protoClass;
        this.identity = identity;
    }

    public void handler() throws NullPointerException {
        Log.i("MSG", "MessageHandler handler : " + message);
    }

    public void timeOut() {
        Log.i("MSG", "MessageHandler timeOut : " + message);
        error();
    }

    public void error() {
        Log.i("MSG", "MessageHandler error : " + message);
    }

}
