package com.iGap.response;

import com.iGap.helper.HelperRealm;
import com.iGap.proto.ProtoClientGetRoom;

public class ClientGetRoomResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public ClientGetRoomResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }


    @Override
    public void handler() {
        ProtoClientGetRoom.ClientGetRoomResponse.Builder clientGetRoom = (ProtoClientGetRoom.ClientGetRoomResponse.Builder) message;
        HelperRealm.convert(clientGetRoom.getRoom());
        //TODO [Saeed Mozaffari] [2016-09-03 12:05 PM] - if room is new , update rooms list
    }

    @Override
    public void timeOut() {
    }

    @Override
    public void error() {
    }
}


