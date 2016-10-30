package com.iGap.request;

import com.iGap.proto.ProtoUserProfileGetNickname;

public class RequestUserProfileGetNickname {

    public void userProfileGetNickname() {

        ProtoUserProfileGetNickname.UserProfileGetNickname.Builder builder =
            ProtoUserProfileGetNickname.UserProfileGetNickname.newBuilder();

        RequestWrapper requestWrapper = new RequestWrapper(112, builder);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}