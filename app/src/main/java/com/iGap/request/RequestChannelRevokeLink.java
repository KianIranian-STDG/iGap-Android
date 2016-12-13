
package com.iGap.request;

import com.iGap.proto.ProtoChannelRevokeLink;

public class RequestChannelRevokeLink {

    public void channelRevokeLink(long roomId) {

        ProtoChannelRevokeLink.ChannelRevokeLink.Builder builder = ProtoChannelRevokeLink.ChannelRevokeLink.newBuilder();
        builder.setRoomId(roomId);

        RequestWrapper requestWrapper = new RequestWrapper(421, builder);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
