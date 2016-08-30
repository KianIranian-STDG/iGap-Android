package com.iGap.response;

import com.iGap.proto.ProtoChatGetRoom;

public class ChatGetRoomResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public ChatGetRoomResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }


    @Override
    public void handler() {
        ProtoChatGetRoom.ChatGetRoomResponse.Builder chatGetRoomResponse = (ProtoChatGetRoom.ChatGetRoomResponse.Builder) message;
        chatGetRoomResponse.getRoomId();
    }

    @Override
    public void timeOut() {
    }

    @Override
    public void error() {
    }
}


