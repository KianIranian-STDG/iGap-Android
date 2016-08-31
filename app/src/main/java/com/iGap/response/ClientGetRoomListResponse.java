package com.iGap.response;

import android.util.Log;

import com.iGap.G;
import com.iGap.proto.ProtoClientGetRoomList;
import com.iGap.proto.ProtoError;
import com.iGap.proto.ProtoResponse;

public class ClientGetRoomListResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public ClientGetRoomListResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }


    @Override
    public void handler() {

        ProtoClientGetRoomList.ClientGetRoomListResponse.Builder clientGetRoomListResponse = (ProtoClientGetRoomList.ClientGetRoomListResponse.Builder) message;
        ProtoResponse.Response.Builder response = ProtoResponse.Response.newBuilder().mergeFrom(clientGetRoomListResponse.getResponse());

        Log.i("SOC", "ClientGetRoomListResponse response.getId() : " + response.getId());
        Log.i("SOC", "ClientGetRoomListResponse response.getTimestamp() : " + response.getTimestamp());
        G.onClientGetRoomListResponse.onClientGetRoomList(clientGetRoomListResponse.getRoomsList(), clientGetRoomListResponse.getResponse());
    }

    @Override
    public void timeOut() {
        Log.i("SOC", "ClientGetRoomListResponse timeout");
    }

    @Override
    public void error() {
        ProtoError.ErrorResponse.Builder errorResponse = (ProtoError.ErrorResponse.Builder) message;
        int majorCode = errorResponse.getMajorCode();
        int minorCode = errorResponse.getMinorCode();

        Log.i("SOC", "ClientGetRoomListResponse response.majorCode() : " + majorCode);
        Log.i("SOC", "ClientGetRoomListResponse response.minorCode() : " + minorCode);
    }
}


