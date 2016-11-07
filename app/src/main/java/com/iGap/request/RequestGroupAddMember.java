package com.iGap.request;

import com.iGap.proto.ProtoGlobal;
import com.iGap.proto.ProtoGroupAddMember;

public class RequestGroupAddMember {

    public void groupAddMember(long roomId, long userId, long startMessageId, ProtoGlobal.GroupRoom.Role role) {

        ProtoGroupAddMember.GroupAddMember.Member.Builder member = ProtoGroupAddMember.GroupAddMember.Member.newBuilder();
        member.setUserId(userId);
        member.setStartMessageId(startMessageId);

        ProtoGroupAddMember.GroupAddMember.Builder builder = ProtoGroupAddMember.GroupAddMember.newBuilder();
        builder.setRoomId(roomId);
        builder.setMember(member);

        RequestWrapper requestWrapper = new RequestWrapper(301, builder);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}

