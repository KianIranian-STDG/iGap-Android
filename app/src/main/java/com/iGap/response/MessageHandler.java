package com.iGap.response;

/**
 * Created by android on 8/2/2016.
 */
public abstract class MessageHandler {

    int actionId;
    public Object message;
    String identity;

    public MessageHandler(int actionId, Object protoClass, String identity) {

        this.actionId = actionId;
        this.message = protoClass;
        this.identity = identity;
    }

    public abstract void handler() throws NullPointerException;

    public void timeOut() {
        error();
    }

    public abstract void error();

}
