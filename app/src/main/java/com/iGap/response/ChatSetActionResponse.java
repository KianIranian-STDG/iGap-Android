package com.iGap.response;

import com.iGap.G;
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
        super.handler();
        ProtoChatSetAction.ChatSetActionResponse.Builder builder = (ProtoChatSetAction.ChatSetActionResponse.Builder) message;

        if (G.onSetAction != null) {
            G.onSetAction.onSetAction(builder.getRoomId(), builder.getUserId(), builder.getAction());
        }
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


