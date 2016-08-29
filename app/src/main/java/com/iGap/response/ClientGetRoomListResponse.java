package com.iGap.response;

import com.iGap.proto.ProtoClientGetRoomList;
import com.iGap.proto.ProtoGlobal;

public class ClientGetRoomListResponse extends MessageHandler {

    public int actionId;
    public Object message;

    public ClientGetRoomListResponse(int actionId, Object protoClass) {
        super(actionId, protoClass);

        this.message = protoClass;
        this.actionId = actionId;
    }


    @Override
    public void handler() {

        ProtoClientGetRoomList.ClientGetRoomListResponse.Builder clientGetRoomList = (ProtoClientGetRoomList.ClientGetRoomListResponse.Builder) message;

        for (ProtoGlobal.Room room : clientGetRoomList.getRoomsList()) {


        }




    }

    @Override
    public void timeOut() {
    }

    @Override
    public void error() {
    }
}


