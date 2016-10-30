package com.iGap.request;

import com.iGap.proto.ProtoGroupAddAdmin;

public class RequestGroupAddAdmin {

    public void groupAddAdmin(long roomId, long memberId) {

        ProtoGroupAddAdmin.GroupAddAdmin.Builder builder =
            ProtoGroupAddAdmin.GroupAddAdmin.newBuilder();
        builder.setRoomId(roomId);
        builder.setMemberId(memberId);

        RequestWrapper requestWrapper = new RequestWrapper(302, builder);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}

