
package com.iGap.request;

import com.iGap.proto.ProtoClientJoinByUsername;

public class RequestClientJoinByUsername {

    public void clientJoinByUsername(String username) {

        ProtoClientJoinByUsername.ClientJoinByUsername.Builder builder = ProtoClientJoinByUsername.ClientJoinByUsername.newBuilder();
        builder.setUsername(username);

        RequestWrapper requestWrapper = new RequestWrapper(609, builder);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
