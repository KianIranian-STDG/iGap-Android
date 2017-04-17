package com.iGap.response;

import com.iGap.proto.ProtoError;

public class ErrorResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public ErrorResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();

        ProtoError.ErrorResponse.Builder errorResponse = (ProtoError.ErrorResponse.Builder) message;
        double majorCode = errorResponse.getMajorCode();
        double minorCode = errorResponse.getMinorCode();
        int wait = errorResponse.getWait();
        String errorMessage = errorResponse.getMessage();

    }

    @Override
    public void error() {
        super.error();
        ProtoError.ErrorResponse.Builder errorResponse = (ProtoError.ErrorResponse.Builder) message;
        double majorCode = errorResponse.getMajorCode();
        double minorCode = errorResponse.getMinorCode();

    }
}
