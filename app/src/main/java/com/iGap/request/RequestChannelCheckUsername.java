
package com.iGap.request;

import com.iGap.proto.ProtoChannelCheckUsername;

public class RequestChannelCheckUsername {

    public void channelCheckUsername(long roomId, String username) {
        ProtoChannelCheckUsername.ChannelCheckUsername.Builder builder = ProtoChannelCheckUsername.ChannelCheckUsername.newBuilder();
        builder.setRoomId(roomId);
        builder.setUsername(username);

        RequestWrapper requestWrapper = new RequestWrapper(418, builder);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
