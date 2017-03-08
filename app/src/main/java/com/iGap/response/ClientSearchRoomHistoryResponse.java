package com.iGap.response;

import com.iGap.G;
import com.iGap.proto.ProtoClientSearchRoomHistory;
import com.iGap.proto.ProtoError;

public class ClientSearchRoomHistoryResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public ClientSearchRoomHistoryResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);
        this.message = protoClass;
        this.identity = identity;
        this.actionId = actionId;
    }

    @Override
    public void handler() {
        super.handler();
        ProtoClientSearchRoomHistory.ClientSearchRoomHistoryResponse.Builder builder = (ProtoClientSearchRoomHistory.ClientSearchRoomHistoryResponse.Builder) message;
        G.onClientSearchRoomHistory.onClientSearchRoomHistory(builder.getTotalCount(), builder.getNotDeletedCount(), builder.getResultList(), identity);
    }


    @Override
    public void error() {
        super.error();

        ProtoError.ErrorResponse.Builder errorResponse = (ProtoError.ErrorResponse.Builder) message;
        int majorCode = errorResponse.getMajorCode();
        int minorCode = errorResponse.getMinorCode();

        G.onClientSearchRoomHistory.onError(majorCode, minorCode, identity);
    }
}


