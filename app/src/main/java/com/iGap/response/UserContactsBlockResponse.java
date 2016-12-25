package com.iGap.response;

import com.iGap.proto.ProtoUserContactsBlock;

public class UserContactsBlockResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public UserContactsBlockResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();

        ProtoUserContactsBlock.UserContactsBlockResponse.Builder builder = (ProtoUserContactsBlock.UserContactsBlockResponse.Builder) message;
        builder.getUserId();
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


