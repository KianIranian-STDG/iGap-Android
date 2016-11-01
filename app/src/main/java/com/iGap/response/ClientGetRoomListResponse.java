package com.iGap.response;

import com.iGap.G;
import com.iGap.proto.ProtoClientGetRoomList;
import com.iGap.proto.ProtoError;

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

    @Override public void handler() {
        ProtoClientGetRoomList.ClientGetRoomListResponse.Builder clientGetRoomListResponse =
            (ProtoClientGetRoomList.ClientGetRoomListResponse.Builder) message;
        G.onClientGetRoomListResponse.onClientGetRoomList(clientGetRoomListResponse.getRoomsList(),
            clientGetRoomListResponse.getResponse());
    }

    @Override public void timeOut() {
        super.timeOut();
    }

    @Override public void error() {
        super.error();
        ProtoError.ErrorResponse.Builder errorResponse = (ProtoError.ErrorResponse.Builder) message;
        int majorCode = errorResponse.getMajorCode();
        int minorCode = errorResponse.getMinorCode();
        G.onClientGetRoomListResponse.onError(majorCode, minorCode);
    }
}


