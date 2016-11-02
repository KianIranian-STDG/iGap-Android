package com.iGap.request;

import com.iGap.proto.ProtoUserProfileSetSelfRemove;

public class RequestUserProfileSetSelfRemove {

    public void userProfileSetSelfRemove(int numberOfMonth) {

        ProtoUserProfileSetSelfRemove.UserProfileSetSelfRemove.Builder builder =
            ProtoUserProfileSetSelfRemove.UserProfileSetSelfRemove.newBuilder();

        builder.setSelfRemove(numberOfMonth);

        RequestWrapper requestWrapper = new RequestWrapper(120, builder);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}

