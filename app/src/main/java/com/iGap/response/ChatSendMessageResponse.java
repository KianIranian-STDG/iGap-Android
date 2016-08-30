package com.iGap.response;

import com.iGap.proto.ProtoChatSendMessage;

public class ChatSendMessageResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public ChatSendMessageResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);
    }


    @Override
    public void handler() {
        ProtoChatSendMessage.ChatSendMessageResponse.Builder chatSendMessageResponse = (ProtoChatSendMessage.ChatSendMessageResponse.Builder) message;
        chatSendMessageResponse.getRoomId();
        chatSendMessageResponse.getRoomMessage();
    }

    @Override
    public void error() {

    }
}

//    @Override
//    /**
//     * @param ProtoChatClearMessage.ChatClearMessage protoObject
//     */
//    public void handler(int actionId, Class protoObject) {
//
//    }
