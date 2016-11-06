package com.iGap.request;

import com.iGap.proto.ProtoGroupDelete;

public class RequestGroupDelete {

    public void groupDelete(long roomId) {

        ProtoGroupDelete.GroupDelete.Builder builder = ProtoGroupDelete.GroupDelete.newBuilder();
        builder.setRoomId(roomId);

        RequestWrapper requestWrapper = new RequestWrapper(318, builder);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
