package com.iGap.response;

import com.iGap.proto.ProtoChatClearMessage;

public class ChatClearMessageResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public ChatClearMessageResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);
    }


    @Override
    public void handler() {
        ProtoChatClearMessage.ChatClearMessage.Builder chatClearMessage = (ProtoChatClearMessage.ChatClearMessage.Builder) message;
        chatClearMessage.getRoomId();
        chatClearMessage.getClearId();
    }

    @Override
    public void timeOut() {
    }

    @Override
    public void error() {
    }
}


