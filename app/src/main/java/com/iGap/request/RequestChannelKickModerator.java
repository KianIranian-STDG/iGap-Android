
package com.iGap.request;

import com.iGap.proto.ProtoChannelKickModerator;

public class RequestChannelKickModerator {

    public void channelKickModerator(long roomId, long memberId) {

        ProtoChannelKickModerator.ChannelKickModerator.Builder builder = ProtoChannelKickModerator.ChannelKickModerator.newBuilder();
        builder.setRoomId(roomId);
        builder.setMemberId(memberId);

        RequestWrapper requestWrapper = new RequestWrapper(408, builder);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
