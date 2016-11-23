package com.iGap.request;

import com.iGap.proto.ProtoChannelKickAdmin;

public class RequestChannelKickAdmin {

    public void channelKickAdmin(long roomId, long memberId) {

        ProtoChannelKickAdmin.ChannelKickAdmin.Builder builder = ProtoChannelKickAdmin.ChannelKickAdmin.newBuilder();
        builder.setRoomId(roomId);
        builder.setMemberId(memberId);

        RequestWrapper requestWrapper = new RequestWrapper(406, builder);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
