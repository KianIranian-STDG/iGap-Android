package com.iGap.request;

import com.iGap.proto.ProtoUserProfileUpdateUsername;

public class RequestUserProfileUpdateUsername {

    public void userProfileUpdateUsername(String username) {

        ProtoUserProfileUpdateUsername.UserProfileUpdateUsername.Builder builder =
            ProtoUserProfileUpdateUsername.UserProfileUpdateUsername.newBuilder();

        builder.setUsername(username);

        RequestWrapper requestWrapper = new RequestWrapper(123, builder);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}

