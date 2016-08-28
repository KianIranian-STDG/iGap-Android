package com.iGap.response;

import com.iGap.G;
import com.iGap.proto.ProtoInfoCountry;

public class InfoCountryResponse extends MessageHandler {

    public int actionId;
    public Object message;

    public InfoCountryResponse(int actionId, Object protoClass) {
        super(actionId, protoClass);

        this.message = protoClass;
        this.actionId = actionId;
    }


    @Override
    public void handler() {

        ProtoInfoCountry.InfoCountryResponse.Builder infoCountryResponse = (ProtoInfoCountry.InfoCountryResponse.Builder) message;

        G.onInfoCountryResponse.onInfoCountryResponse(
                infoCountryResponse.getCallingCode()
                , infoCountryResponse.getName()
                , infoCountryResponse.getPattern()
                , infoCountryResponse.getRegex()
                , infoCountryResponse.getResponse());

    }

    @Override
    public void timeOut() {
    }

    @Override
    public void error() {
    }
}


