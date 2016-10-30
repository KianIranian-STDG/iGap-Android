package com.iGap.request;

import com.iGap.proto.ProtoInfoCountry;

public class RequestInfoCountry {

    public void infoCountry(String isoCode) {

        ProtoInfoCountry.InfoCountry.Builder infoCountry =
            ProtoInfoCountry.InfoCountry.newBuilder();
        infoCountry.setIsoCode(isoCode);

        RequestWrapper requestWrapper = new RequestWrapper(501, infoCountry);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
