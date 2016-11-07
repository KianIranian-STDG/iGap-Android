package com.iGap.request;

import com.iGap.proto.ProtoGroupKickMember;

public class RequestGroupKickMember {

    public void groupKickMember(long roomId, long memberId) {

        ProtoGroupKickMember.GroupKickMember.Builder builder =
                ProtoGroupKickMember.GroupKickMember.newBuilder();
        builder.setRoomId(roomId);
        builder.setMemberId(memberId);

        RequestWrapper requestWrapper = new RequestWrapper(307, builder);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}

