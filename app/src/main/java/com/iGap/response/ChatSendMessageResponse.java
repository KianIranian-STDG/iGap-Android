package com.iGap.response;

public class ChatSendMessageResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public ChatSendMessageResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }


    @Override
    public void handler() {

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
