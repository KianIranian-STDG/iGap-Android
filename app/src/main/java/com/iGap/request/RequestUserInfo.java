package com.iGap.request;

import com.iGap.proto.ProtoUserInfo;

public class RequestUserInfo {
    public void userInfo(long userId) {
        ProtoUserInfo.UserInfo.Builder builder = ProtoUserInfo.UserInfo.newBuilder();
        builder.setUserId(userId);

        RequestWrapper requestWrapper = new RequestWrapper(117, builder);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}

