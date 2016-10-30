package com.iGap.request;

import com.iGap.proto.ProtoUserUsernameToId;

public class RequestUserUsernameToId {

    public void userUserNameToId(String username) {

        ProtoUserUsernameToId.UserUsernameToId.Builder builder =
            ProtoUserUsernameToId.UserUsernameToId.newBuilder();
        builder.setUsername(username);

        RequestWrapper requestWrapper = new RequestWrapper(113, builder);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}