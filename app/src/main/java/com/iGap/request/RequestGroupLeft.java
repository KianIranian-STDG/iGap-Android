package com.iGap.request;

import com.iGap.proto.ProtoGroupLeft;

public class RequestGroupLeft {

    public void groupLeft(long roomId) {

        ProtoGroupLeft.GroupLeft.Builder builder = ProtoGroupLeft.GroupLeft.newBuilder();
        builder.setRoomId(roomId);

        RequestWrapper requestWrapper = new RequestWrapper(309, builder);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

}

