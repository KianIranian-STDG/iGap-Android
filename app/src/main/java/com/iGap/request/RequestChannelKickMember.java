
package com.iGap.request;

import com.iGap.proto.ProtoChannelKickMember;

public class RequestChannelKickMember {

    public void channelKickMember(long roomId, long memberId) {
        ProtoChannelKickMember.ChannelKickMember.Builder builder = ProtoChannelKickMember.ChannelKickMember.newBuilder();
        builder.setRoomId(roomId);
        builder.setMemberId(memberId);

        RequestWrapper requestWrapper = new RequestWrapper(407, builder);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
