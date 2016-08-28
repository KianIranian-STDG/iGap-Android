package com.iGap.request;

import com.iGap.G;
import com.iGap.helper.HelperString;
import com.iGap.proto.ProtoInfoCountry;
import com.iGap.proto.ProtoRequest;
import com.iGap.realm.RealmUserInfo;

public class RequestInfoCountry {

    public void infoCountry() {

        ProtoInfoCountry.InfoCountry.Builder infoCountry = ProtoInfoCountry.InfoCountry.newBuilder();
        infoCountry.setRequest(ProtoRequest.Request.newBuilder().setId(HelperString.generateKey()));
        infoCountry.setIsoCode(G.realm.where(RealmUserInfo.class).findFirst().getCountryISOCode());

        RequestWrapper requestWrapper = new RequestWrapper(501, infoCountry);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
