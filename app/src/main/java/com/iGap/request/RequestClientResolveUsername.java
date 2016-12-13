
package com.iGap.request;

import com.iGap.proto.ProtoClientResolveUsername;

public class RequestClientResolveUsername {

    public void channelAddMessageReaction(String username) {
        ProtoClientResolveUsername.ClientResolveUsername.Builder builder = ProtoClientResolveUsername.ClientResolveUsername.newBuilder();
        builder.setUsername(username);

        RequestWrapper requestWrapper = new RequestWrapper(606, builder);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
