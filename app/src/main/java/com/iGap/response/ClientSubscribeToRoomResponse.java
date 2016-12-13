package com.iGap.response;

import com.iGap.G;
import com.iGap.proto.ProtoError;

public class ClientSubscribeToRoomResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public ClientSubscribeToRoomResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();

        if (G.onClientSubscribeToRoom != null) {
            G.onClientSubscribeToRoom.onClientSubscribeToRoom();
        }
    }

    @Override
    public void timeOut() {
        super.timeOut();
    }

    @Override
    public void error() {
        super.error();
        ProtoError.ErrorResponse.Builder errorResponse = (ProtoError.ErrorResponse.Builder) message;
        int majorCode = errorResponse.getMajorCode();
        int minorCode = errorResponse.getMinorCode();
        if (G.onClientSubscribeToRoom != null) {
            G.onClientSubscribeToRoom.onError(majorCode, minorCode);
        }
    }
}


