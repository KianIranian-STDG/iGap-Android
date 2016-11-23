
package com.iGap.request;

import com.iGap.proto.ProtoChannelAddMember;

public class RequestChannelAddMember {

    public void channelAddMember(long roomId, ProtoChannelAddMember.ChannelAddMember.Member member) {
        ProtoChannelAddMember.ChannelAddMember.Builder builder = ProtoChannelAddMember.ChannelAddMember.newBuilder();
        builder.setRoomId(roomId);
        builder.setMember(member);

        RequestWrapper requestWrapper = new RequestWrapper(401, builder);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
