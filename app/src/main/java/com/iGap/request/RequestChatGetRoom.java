package com.iGap.request;

import com.iGap.proto.ProtoClientCondition;

public class RequestChatGetRoom {

    public void clientCondition() {

        ProtoClientCondition.ClientCondition.Room.Builder room = ProtoClientCondition.ClientCondition.Room.newBuilder();

        ProtoClientCondition.ClientCondition.Builder clientCondition = ProtoClientCondition.ClientCondition.newBuilder();


        RequestWrapper requestWrapper = new RequestWrapper(600, clientCondition);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

}