package com.iGap.request;

import com.iGap.proto.ProtoUserGetDeleteToken;

public class RequestUserGetDeleteToken {

    public void userGetDeleteToken() {

        ProtoUserGetDeleteToken.UserGetDeleteToken.Builder builder =
            ProtoUserGetDeleteToken.UserGetDeleteToken.newBuilder();

        RequestWrapper requestWrapper = new RequestWrapper(118, builder);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
