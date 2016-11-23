
package com.iGap.request;

import com.iGap.proto.ProtoChannelLeft;

public class RequestChannelLeft {

    public void channelLeft(long roomId) {

        ProtoChannelLeft.ChannelLeft.Builder builder = ProtoChannelLeft.ChannelLeft.newBuilder();
        builder.setRoomId(roomId);

        RequestWrapper requestWrapper = new RequestWrapper(409, builder);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
