package com.iGap.request;

import com.iGap.proto.ProtoClientSearchUsername;

public class RequestClientSearchUsername {

    public void channelGetMessagesStats(String query) {

        ProtoClientSearchUsername.ClientSearchUsername.Builder builder = ProtoClientSearchUsername.ClientSearchUsername.newBuilder();
        builder.setQuery(query);

        RequestWrapper requestWrapper = new RequestWrapper(612, builder);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
