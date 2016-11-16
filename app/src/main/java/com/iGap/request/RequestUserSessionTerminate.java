package com.iGap.request;

import com.iGap.proto.ProtoUserSessionTerminate;

public class RequestUserSessionTerminate {

    public void userSessionTerminate(long sessionId) {

        ProtoUserSessionTerminate.UserSessionTerminate.Builder builder = ProtoUserSessionTerminate.UserSessionTerminate.newBuilder();
        builder.setSessionId(sessionId);

        RequestWrapper requestWrapper = new RequestWrapper(126, builder);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

}

