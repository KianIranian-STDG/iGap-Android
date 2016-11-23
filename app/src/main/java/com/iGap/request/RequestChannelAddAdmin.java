
package com.iGap.request;

import com.iGap.proto.ProtoChannelAddAdmin;

public class RequestChannelAddAdmin {

    public void channelAddAdmin(long roomId, long memberId) {
        ProtoChannelAddAdmin.ChannelAddAdmin.Builder builder = ProtoChannelAddAdmin.ChannelAddAdmin.newBuilder();
        builder.setRoomId(roomId);
        builder.setMemberId(memberId);

        RequestWrapper requestWrapper = new RequestWrapper(402, builder);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
