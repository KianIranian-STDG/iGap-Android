package com.iGap.request;

import com.iGap.proto.ProtoUserProfileGetGender;

public class RequestUserProfileGetGender {

    public void userProfileGetGender() {

        ProtoUserProfileGetGender.UserProfileGetGender.Builder builder =
            ProtoUserProfileGetGender.UserProfileGetGender.newBuilder();

        RequestWrapper requestWrapper = new RequestWrapper(111, builder);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}

