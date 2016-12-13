
package com.iGap.request;

import com.iGap.proto.ProtoChannelRemoveUsername;

public class RequestChannelRemoveUsername {

    public void channelRemoveUsername(long roomId) {

        ProtoChannelRemoveUsername.ChannelRemoveUsername.Builder builder = ProtoChannelRemoveUsername.ChannelRemoveUsername.newBuilder();
        builder.setRoomId(roomId);

        RequestWrapper requestWrapper = new RequestWrapper(420, builder);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
