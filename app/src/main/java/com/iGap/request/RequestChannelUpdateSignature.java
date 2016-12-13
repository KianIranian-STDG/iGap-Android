
package com.iGap.request;

import com.iGap.proto.ProtoChannelUpdateSignature;

public class RequestChannelUpdateSignature {

    public void channelUpdateSignature(long roomId, boolean signature) {
        ProtoChannelUpdateSignature.ChannelUpdateSignature.Builder builder = ProtoChannelUpdateSignature.ChannelUpdateSignature.newBuilder();
        builder.setRoomId(roomId);
        builder.setSignature(signature);

        RequestWrapper requestWrapper = new RequestWrapper(422, builder);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
