package com.iGap.request;

import com.iGap.helper.HelperString;
import com.iGap.proto.ProtoInfoTime;
import com.iGap.proto.ProtoRequest;

public class RequestInfoTime {

    public void infoTime() {

        ProtoInfoTime.InfoTime.Builder infoTime = ProtoInfoTime.InfoTime.newBuilder();
        infoTime.setRequest(ProtoRequest.Request.newBuilder().setId(HelperString.generateKey()));

        RequestWrapper requestWrapper = new RequestWrapper(502, infoTime);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
