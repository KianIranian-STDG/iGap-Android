package com.iGap.response;

/**
 * Created by android on 8/2/2016.
 */
public abstract class MessageHandler {

    int actionId;
    public Object message;

    public MessageHandler(int actionId, Object protoClass) {

    }

    public abstract void handler() throws NullPointerException;

    public void timeOut() {
        error();
    }

    public abstract void error();

}
