package com.iGap.response;

import com.iGap.proto.ProtoChatUpdateStatus;

public class ChatUpdateStatusResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public ChatUpdateStatusResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);
    }


    @Override
    public void handler() {
        ProtoChatUpdateStatus.ChatUpdateStatusResponse.Builder chatUpdateStatus = (ProtoChatUpdateStatus.ChatUpdateStatusResponse.Builder) message;
        chatUpdateStatus.getRoomId();
        chatUpdateStatus.getMessageId();
        chatUpdateStatus.getStatus();
        chatUpdateStatus.getStatusVersion();
    }

    @Override
    public void timeOut() {
    }

    @Override
    public void error() {
    }
}


