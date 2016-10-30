package com.iGap.response;

import com.iGap.G;
import com.iGap.proto.ProtoError;
import com.iGap.proto.ProtoInfoTime;

public class InfoTimeResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public InfoTimeResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override public void handler() {
        //G.handlerCount += 1;
        ProtoInfoTime.InfoTimeResponse.Builder infoTime =
            (ProtoInfoTime.InfoTimeResponse.Builder) message;
        G.onInfoTime.onInfoTime(infoTime.getTimestamp(), infoTime.getResponse());
    }

    @Override public void timeOut() {
        //G.timeoutCount += 1;
    }

    @Override public void error() {
        //G.errorCount += 1;
        ProtoError.ErrorResponse.Builder errorResponse = (ProtoError.ErrorResponse.Builder) message;
        int majorCode = errorResponse.getMajorCode();
        int minorCode = errorResponse.getMinorCode();
    }
}


