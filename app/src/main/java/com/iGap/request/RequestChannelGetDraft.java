
package com.iGap.request;

import com.iGap.proto.ProtoChannelGetDraft;

public class RequestChannelGetDraft {

    public void channelGetDraft(long roomId) {
        ProtoChannelGetDraft.ChannelGetDraft.Builder builder = ProtoChannelGetDraft.ChannelGetDraft.newBuilder();
        builder.setRoomId(roomId);

        RequestWrapper requestWrapper = new RequestWrapper(416, builder);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
