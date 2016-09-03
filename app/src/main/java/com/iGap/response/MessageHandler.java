package com.iGap.response;

public abstract class MessageHandler { //TODO [Saeed Mozaffari] [2016-09-01 9:47 AM] - MessageHandler extends from ActivityEnhanced for instance and close realm, test it

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
