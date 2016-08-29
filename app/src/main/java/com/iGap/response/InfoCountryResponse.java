package com.iGap.response;

import com.iGap.G;
import com.iGap.proto.ProtoInfoCountry;

public class InfoCountryResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public InfoCountryResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
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


