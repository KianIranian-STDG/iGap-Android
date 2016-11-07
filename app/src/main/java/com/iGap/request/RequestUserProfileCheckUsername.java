package com.iGap.request;

import com.iGap.proto.ProtoUserProfileCheckUsername;

public class RequestUserProfileCheckUsername {

    public void userProfileCheckUsername(String username) {

        ProtoUserProfileCheckUsername.UserProfileCheckUsername.Builder builder =
                ProtoUserProfileCheckUsername.UserProfileCheckUsername.newBuilder();

        builder.setUsername(username);

        RequestWrapper requestWrapper = new RequestWrapper(122, builder);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}

