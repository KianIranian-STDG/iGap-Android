package com.iGap.request;

import com.iGap.helper.HelperString;
import com.iGap.proto.ProtoInfoLocation;
import com.iGap.proto.ProtoRequest;

public class RequestInfoLocation {

    public void infoLocation() {
        ProtoInfoLocation.InfoLocation.Builder infoLocation = ProtoInfoLocation.InfoLocation.newBuilder();
        infoLocation.setRequest(ProtoRequest.Request.newBuilder().setId(HelperString.generateKey()));

        RequestWrapper requestWrapper = new RequestWrapper(500, infoLocation);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
