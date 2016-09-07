package com.iGap.response;

import android.util.Log;

import com.iGap.proto.ProtoClientGetRoomHistory;

public class ClientGetRoomHistoryResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public ClientGetRoomHistoryResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.actionId = actionId;
        this.message = protoClass;
        this.identity = identity;
    }


    @Override
    public void handler() {

        Log.i("HHH", "ClientGetRoomHistoryResponse message : " + message);
        ProtoClientGetRoomHistory.ClientGetRoomHistoryResponse.Builder builder = (ProtoClientGetRoomHistory.ClientGetRoomHistoryResponse.Builder) message;
        builder.getMessageList();
    }

    @Override
    public void timeOut() {
    }

    @Override
    public void error() {
    }
}


