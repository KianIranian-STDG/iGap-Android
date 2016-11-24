package com.iGap.response;

import com.iGap.G;
import com.iGap.proto.ProtoError;
import com.iGap.proto.ProtoInfoLocation;

public class InfoLocationResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public InfoLocationResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();
        ProtoInfoLocation.InfoLocationResponse.Builder infoLocationResponse =
                (ProtoInfoLocation.InfoLocationResponse.Builder) message;

        G.onReceiveInfoLocation.onReceive(infoLocationResponse.getIsoCode(),
                infoLocationResponse.getCallingCode(), infoLocationResponse.getName(),
                infoLocationResponse.getPattern(), infoLocationResponse.getRegex());
    }

    @Override
    public void timeOut() {
        super.timeOut();
    }

    @Override
    public void error() {
        super.error();
        ProtoError.ErrorResponse.Builder errorResponse = (ProtoError.ErrorResponse.Builder) message;
        //        errorResponse.getMajorCode();
        //        errorResponse.getMinorCode();


        //G.onReceiveInfoLocation.onReceive("IR", 98, "Iran Test", "pattern", "Regex");
    }
}
