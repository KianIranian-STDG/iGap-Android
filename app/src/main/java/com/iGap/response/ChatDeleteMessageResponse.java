package com.iGap.response;

import com.iGap.proto.ProtoChatDeleteMessage;

public class ChatDeleteMessageResponse extends MessageHandler {

//    public int actionId;
//    public Object message;
//    public String identity;

    public ChatDeleteMessageResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }


    @Override
    public void handler() {

        ProtoChatDeleteMessage.ChatDeleteMessageResponse.Builder chatDeleteMessage = (ProtoChatDeleteMessage.ChatDeleteMessageResponse.Builder) message;
        chatDeleteMessage.getRoomId();
        chatDeleteMessage.getMessageId();
        chatDeleteMessage.clearDeleteVersion();

    }

    @Override
    public void timeOut() {
    }

    @Override
    public void error() {
    }
}


