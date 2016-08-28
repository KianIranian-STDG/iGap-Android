package com.iGap.request;

import com.iGap.helper.HelperString;
import com.iGap.proto.ProtoRequest;
import com.iGap.proto.ProtoUserProfileNickname;

public class RequestUserProfileNickName {

    public void userProfileNickName(String nickName) {
        ProtoUserProfileNickname.UserProfileNickname.Builder userProfileNickName = ProtoUserProfileNickname.UserProfileNickname.newBuilder();
        userProfileNickName.setRequest(ProtoRequest.Request.newBuilder().setId(HelperString.generateKey()));
        userProfileNickName.setNickname(nickName);

        RequestWrapper requestWrapper = new RequestWrapper(105, userProfileNickName);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

}