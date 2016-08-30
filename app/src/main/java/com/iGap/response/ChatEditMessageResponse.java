package com.iGap.response;

import com.iGap.proto.ProtoChatEditMessage;

public class ChatEditMessageResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public ChatEditMessageResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);
    }


    @Override
    public void handler() {
        ProtoChatEditMessage.ChatEditMessageResponse.Builder chatEditMessageResponse = (ProtoChatEditMessage.ChatEditMessageResponse.Builder) message;
        chatEditMessageResponse.getRoomId();
        chatEditMessageResponse.getMessageId();
        chatEditMessageResponse.getMessageVersion();
        chatEditMessageResponse.getMessage();
    }

    @Override
    public void timeOut() {
    }

    @Override
    public void error() {
    }
}


