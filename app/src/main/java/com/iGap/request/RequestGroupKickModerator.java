package com.iGap.request;

import com.iGap.proto.ProtoGroupKickModerator;

public class RequestGroupKickModerator {

    public void groupKickModerator(long roomId, long memberId) {

        ProtoGroupKickModerator.GroupKickModerator.Builder builder =
                ProtoGroupKickModerator.GroupKickModerator.newBuilder();
        builder.setRoomId(roomId);
        builder.setMemberId(memberId);

        RequestWrapper requestWrapper = new RequestWrapper(308, builder);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}

