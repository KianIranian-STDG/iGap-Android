
package com.iGap.request;

import com.iGap.proto.ProtoChannelAddModerator;

public class RequestChannelAddModerator {

    public void channelAddModerator(long roomId, long memberId) {

        ProtoChannelAddModerator.ChannelAddModerator.Builder builder = ProtoChannelAddModerator.ChannelAddModerator.newBuilder();
        builder.setRoomId(roomId);
        builder.setMemberId(memberId);

        RequestWrapper requestWrapper = new RequestWrapper(403, builder);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
