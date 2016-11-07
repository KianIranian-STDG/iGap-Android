package com.iGap.request;

import com.iGap.proto.ProtoGroupKickAdmin;

public class RequestGroupKickAdmin {

    public void groupKickAdmin(long roomId, long memberId) {

        ProtoGroupKickAdmin.GroupKickAdmin.Builder builder =
                ProtoGroupKickAdmin.GroupKickAdmin.newBuilder();
        builder.setRoomId(roomId);
        builder.setMemberId(memberId);

        RequestWrapper requestWrapper = new RequestWrapper(306, builder);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}

