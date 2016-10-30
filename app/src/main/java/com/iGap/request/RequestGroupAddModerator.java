package com.iGap.request;

import com.iGap.proto.ProtoGroupAddModerator;

public class RequestGroupAddModerator {

    public void groupAddModerator(long roomId, long memberId) {

        ProtoGroupAddModerator.GroupAddModerator.Builder builder =
            ProtoGroupAddModerator.GroupAddModerator.newBuilder();
        builder.setRoomId(roomId);
        builder.setMemberId(memberId);

        RequestWrapper requestWrapper = new RequestWrapper(303, builder);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}

