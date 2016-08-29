package com.iGap.response;

public class ClientGetRoomList extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public ClientGetRoomList(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }


    @Override
    public void handler() {
    }

    @Override
    public void timeOut() {
    }

    @Override
    public void error() {
    }
}


