package com.iGap.request;

import com.iGap.proto.ProtoUserAvatarGetList;

public class RequestUserAvatarGetList {


    public void userAddGetList(long userId) {

        ProtoUserAvatarGetList.UserAvatarGetList.Builder builder = ProtoUserAvatarGetList.UserAvatarGetList.newBuilder();
        builder.setUserId(userId);

        RequestWrapper requestWrapper = new RequestWrapper(116, builder);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

}

