package com.iGap.response;

public class ChatSendMessageResponse extends MessageHandler {

    public int actionId;
    public Object message;

    public ChatSendMessageResponse(int actionId, Object protoClass) {
        super(actionId, protoClass);

        this.message = protoClass;
        this.actionId = actionId;
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
