package com.iGap.request;

import android.util.Log;
import com.iGap.proto.ProtoUserProfileNickname;

public class RequestUserProfileSetNickname {

    public void userProfileNickName(String nickName) {
        ProtoUserProfileNickname.UserProfileSetNickname.Builder userProfileNickName = ProtoUserProfileNickname.UserProfileSetNickname.newBuilder();
        userProfileNickName.setNickname(nickName);

        Log.i("SSSS", "userProfileNickName : " + userProfileNickName);
        RequestWrapper requestWrapper = new RequestWrapper(105, userProfileNickName);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}