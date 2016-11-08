package com.iGap.response;

import com.iGap.proto.ProtoChatSetAction;

public class ChatSetActionResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public ChatSetActionResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        ProtoChatSetAction.ChatSetActionResponse.Builder builder = (ProtoChatSetAction.ChatSetActionResponse.Builder) message;
        builder.getUserId();
        builder.getRoomId();
        builder.getAction();
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


