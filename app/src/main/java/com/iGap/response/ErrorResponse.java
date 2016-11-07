package com.iGap.response;

import android.util.Log;

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
        Log.i("SOC", "ErrorResponse");
        ProtoError.ErrorResponse.Builder errorResponse = (ProtoError.ErrorResponse.Builder) message;
        double majorCode = errorResponse.getMajorCode();
        double minorCode = errorResponse.getMinorCode();
        Log.i("SOC", "majorCode : " + majorCode);
        Log.i("SOC", "minorCode : " + minorCode);
    }

    @Override
    public void error() {
        Log.i("SOC", "1ErrorResponse");
        ProtoError.ErrorResponse.Builder errorResponse = (ProtoError.ErrorResponse.Builder) message;
        double majorCode = errorResponse.getMajorCode();
        double minorCode = errorResponse.getMinorCode();
        Log.i("SOC", "1majorCode : " + majorCode);
        Log.i("SOC", "1minorCode : " + minorCode);
    }
}
