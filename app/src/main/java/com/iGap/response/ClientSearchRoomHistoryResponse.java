package com.iGap.response;

import android.util.Log;

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

        ProtoClientSearchRoomHistory.ClientSearchRoomHistoryResponse.Builder builder = (ProtoClientSearchRoomHistory.ClientSearchRoomHistoryResponse.Builder) message;
//        builder.getTotalCount();
//        builder.getNotDeletedCount();
//        builder.getResultList();

        G.onClientSearchRoomHistory.onClientSearchRoomHistory(builder.getTotalCount(), builder.getNotDeletedCount(), builder.getResultList());

    }

    @Override
    public void timeOut() {
        super.timeOut();

        G.onClientSearchRoomHistory.onTimeOut();
    }

    @Override
    public void error() {
        super.error();

        ProtoError.ErrorResponse.Builder errorResponse = (ProtoError.ErrorResponse.Builder) message;
        int majorCode = errorResponse.getMajorCode();
        int minorCode = errorResponse.getMinorCode();
        Log.i("XXX", "GroupEditResponse majorCode : " + majorCode);
        Log.i("XXX", "GroupEditResponse minorCode : " + minorCode);

        G.onClientSearchRoomHistory.onError(majorCode, minorCode);
    }
}


