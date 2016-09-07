package com.iGap.response;

import com.iGap.proto.ProtoChatDelete;

public class ChatDeleteResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public ChatDeleteResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.actionId = actionId;
        this.message = protoClass;
        this.identity = identity;
    }


    @Override
    public void handler() {

        ProtoChatDelete.ChatDeleteResponse.Builder builder = (ProtoChatDelete.ChatDeleteResponse.Builder) message;
        builder.getRoomId();
    }

    @Override
    public void timeOut() {
    }

    @Override
    public void error() {
    }
}


