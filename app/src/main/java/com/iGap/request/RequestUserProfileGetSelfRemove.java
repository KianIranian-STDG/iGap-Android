package com.iGap.request;

import com.iGap.proto.ProtoUserProfileGetSelfRemove;

public class RequestUserProfileGetSelfRemove {

    public void userProfileGetSelfRemove() {

        ProtoUserProfileGetSelfRemove.UserProfileGetSelfRemove.Builder builder =
            ProtoUserProfileGetSelfRemove.UserProfileGetSelfRemove.newBuilder();

        RequestWrapper requestWrapper = new RequestWrapper(121, builder);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}

