package com.iGap.request;

import com.iGap.proto.ProtoUserSessionLogout;

public class RequestUserSessionLogout {

    public void userSessionLogout() {
        ProtoUserSessionLogout.UserSessionLogout.Builder builder = ProtoUserSessionLogout.UserSessionLogout.newBuilder();
        RequestWrapper requestWrapper = new RequestWrapper(127, builder);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}