package com.iGap.request;

import com.iGap.proto.ProtoChannelCreate;

public class RequestChannelCreate {

    public void channelCreate(String name, String description) {
        ProtoChannelCreate.ChannelCreate.Builder builder = ProtoChannelCreate.ChannelCreate.newBuilder();
        builder.setName(name);
        builder.setDescription(description);

        RequestWrapper requestWrapper = new RequestWrapper(400, builder);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
