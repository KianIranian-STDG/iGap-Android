package com.iGap.response;

import android.util.Log;

import com.iGap.G;
import com.iGap.proto.ProtoError;
import com.iGap.proto.ProtoInfoPage;
import com.iGap.proto.ProtoResponse;

public class InfoPageResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public InfoPageResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }


    @Override
    public void handler() {

        ProtoInfoPage.InfoPageResponse.Builder infoPageResponse = (ProtoInfoPage.InfoPageResponse.Builder) message;
        String body = infoPageResponse.getBody();

        Log.i("SOC", "InfoTimeResponse getBody : " + body);

        ProtoResponse.Response.Builder response = ProtoResponse.Response.newBuilder().mergeFrom(infoPageResponse.getResponse());
        Log.i("SOC", "InfoTimeResponse response.getId() : " + response.getId());
        Log.i("SOC", "InfoTimeResponse response.getTimestamp() : " + response.getTimestamp());

        G.onReceivePageInfoTOS.onReceivePageInfo(body);
    }

    @Override
    public void timeOut() {
    }

    @Override
    public void error() {
        ProtoError.ErrorResponse.Builder errorResponse = (ProtoError.ErrorResponse.Builder) message;
        int majorCode = errorResponse.getMajorCode();
        int minorCode = errorResponse.getMinorCode();
        Log.i("SOC", "InfoPageResponse majorCode : " + majorCode);
        Log.i("SOC", "InfoPageResponse minorCode : " + minorCode);
    }
}


