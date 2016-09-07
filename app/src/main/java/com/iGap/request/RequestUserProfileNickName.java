package com.iGap.request;

import com.iGap.proto.ProtoUserProfileNickname;

public class RequestUserProfileNickName {

    public void userProfileNickName(String nickName) {
        ProtoUserProfileNickname.UserProfileSetNickname.Builder userProfileNickName = ProtoUserProfileNickname.UserProfileSetNickname.newBuilder();
        userProfileNickName.setNickname(nickName);

        RequestWrapper requestWrapper = new RequestWrapper(105, userProfileNickName);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

}