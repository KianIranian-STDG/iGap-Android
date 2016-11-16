package com.iGap.request;

import com.iGap.proto.ProtoUserSessionGetActiveList;

public class RequestUserSessionGetActiveList {

    public void userSessionGetActiveList() {
        ProtoUserSessionGetActiveList.UserSessionGetActiveList.Builder chatClearMessage = ProtoUserSessionGetActiveList.UserSessionGetActiveList.newBuilder();
        RequestWrapper requestWrapper = new RequestWrapper(125, chatClearMessage);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}

