package com.iGap.response;

import android.util.Log;

public abstract class MessageHandler { //TODO [Saeed Mozaffari] [2016-09-01 9:47 AM] - MessageHandler extends from ActivityEnhanced for instance and close realm, test it

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
