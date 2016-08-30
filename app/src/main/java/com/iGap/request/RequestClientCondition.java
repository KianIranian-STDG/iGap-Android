package com.iGap.request;

import com.iGap.helper.HelperString;
import com.iGap.proto.ProtoClientCondition;
import com.iGap.proto.ProtoRequest;

public class RequestClientCondition {

    public void clientCondition() {

        ProtoClientCondition.ClientCondition.Room.Builder room = ProtoClientCondition.ClientCondition.Room.newBuilder();

        ProtoClientCondition.ClientCondition.Builder clientCondition = ProtoClientCondition.ClientCondition.newBuilder();
        clientCondition.setRequest(ProtoRequest.Request.newBuilder().setId(HelperString.generateKey()));


        RequestWrapper requestWrapper = new RequestWrapper(600, clientCondition);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

}