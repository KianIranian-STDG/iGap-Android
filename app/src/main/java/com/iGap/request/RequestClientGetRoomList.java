package com.iGap.request;

import com.iGap.helper.HelperString;
import com.iGap.proto.ProtoClientGetRoomList;
import com.iGap.proto.ProtoRequest;

public class RequestClientGetRoomList {

    public void clientGetRoomList() {

        ProtoClientGetRoomList.ClientGetRoomList.Builder clientGetRoomList =
                ProtoClientGetRoomList.ClientGetRoomList.newBuilder();
        clientGetRoomList.setRequest(
                ProtoRequest.Request.newBuilder().setId(HelperString.generateKey()));

        RequestWrapper requestWrapper = new RequestWrapper(601, clientGetRoomList);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}