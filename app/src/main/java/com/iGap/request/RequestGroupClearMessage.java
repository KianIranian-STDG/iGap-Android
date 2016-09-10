package com.iGap.request;

import com.iGap.proto.ProtoGroupClearMessage;

public class RequestGroupClearMessage {

    public void groupClearMessage(long roomId, long clearId) {

        ProtoGroupClearMessage.GroupClearMessage.Builder builder = ProtoGroupClearMessage.GroupClearMessage.newBuilder();
        builder.setRoomId(roomId);
        builder.setClearId(clearId);

        RequestWrapper requestWrapper = new RequestWrapper(304, builder);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

}

