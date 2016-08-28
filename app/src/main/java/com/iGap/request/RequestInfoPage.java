package com.iGap.request;

import com.iGap.helper.HelperString;
import com.iGap.proto.ProtoInfoPage;
import com.iGap.proto.ProtoRequest;

public class RequestInfoPage {

    public void infoPage(String id) {
        ProtoInfoPage.InfoPage.Builder infoPage = ProtoInfoPage.InfoPage.newBuilder();
        infoPage.setRequest(ProtoRequest.Request.newBuilder().setId(HelperString.generateKey()));
        infoPage.setId(id);

        RequestWrapper requestWrapper = new RequestWrapper(503, infoPage);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
