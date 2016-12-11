
package com.iGap.request;

import com.iGap.proto.ProtoChannelUpdateUsername;

public class RequestChannelUpdateUsername {

    public void channelUpdateUsername(long roomId, String username) {

        ProtoChannelUpdateUsername.ChannelUpdateUsername.Builder builder = ProtoChannelUpdateUsername.ChannelUpdateUsername.newBuilder();
        builder.setRoomId(roomId);
        builder.setUsername(username);

        RequestWrapper requestWrapper = new RequestWrapper(419, builder);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
