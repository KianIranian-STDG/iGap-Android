package com.iGap.response;

public class UserContactsGetListResponse extends MessageHandler {

    public int actionId;
    public Object message;

    public UserContactsGetListResponse(int actionId, Object protoClass) {
        super(actionId, protoClass);

        this.message = protoClass;
        this.actionId = actionId;
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


