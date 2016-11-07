package com.iGap.response;

import com.iGap.proto.ProtoClientSearchRoomHistory;

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

        ProtoClientSearchRoomHistory.ClientSearchRoomHistoryResponse.Builder builder = (ProtoClientSearchRoomHistory.ClientSearchRoomHistoryResponse.Builder) message;
        builder.getTotalCount();
        builder.getNotDeletedCount();
        builder.getResultList();

    }

    @Override
    public void timeOut() {
        super.timeOut();
    }

    @Override
    public void error() {
        super.error();
    }
}


