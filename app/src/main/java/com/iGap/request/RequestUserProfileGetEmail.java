package com.iGap.request;

import com.iGap.proto.ProtoUserProfileGetEmail;

public class RequestUserProfileGetEmail {


    public void userProfileGetEmail() {

        ProtoUserProfileGetEmail.UserProfileGetEmail.Builder builder = ProtoUserProfileGetEmail.UserProfileGetEmail.newBuilder();

        RequestWrapper requestWrapper = new RequestWrapper(110, builder);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

}

