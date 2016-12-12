package com.iGap.response;

import com.iGap.G;
import com.iGap.proto.ProtoError;

public class UserDeleteResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public UserDeleteResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();
        G.onUserDelete.onUserDeleteResponse();

    }

    @Override
    public void timeOut() {
        super.timeOut();
        G.onUserDelete.TimeOut();
    }

    @Override
    public void error() {
        super.error();

        ProtoError.ErrorResponse.Builder errorReponse = (ProtoError.ErrorResponse.Builder) message;
        errorReponse.getMajorCode();
        errorReponse.getMinorCode();
        G.onUserDelete.Error(errorReponse.getMajorCode(), errorReponse.getMinorCode());
    }

}


