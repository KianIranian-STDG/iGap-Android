package com.iGap.request;

import com.iGap.proto.ProtoGroupGetMemberList;

public class RequestGroupGetMemberList {

    public void getRoomHistory(long roomId) {

        ProtoGroupGetMemberList.GroupGetMemberList.Builder builder =
                ProtoGroupGetMemberList.GroupGetMemberList.newBuilder();

        builder.setRoomId(roomId);

        RequestWrapper requestWrapper = new RequestWrapper(317, builder);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
