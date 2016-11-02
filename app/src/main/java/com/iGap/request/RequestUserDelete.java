package com.iGap.request;

import com.iGap.proto.ProtoUserDelete;

public class RequestUserDelete {

    public void userDelete(String token, ProtoUserDelete.UserDelete.Reason reason) {

        ProtoUserDelete.UserDelete.Builder builder = ProtoUserDelete.UserDelete.newBuilder();
        builder.setToken(token);
        builder.setReason(reason);

        RequestWrapper requestWrapper = new RequestWrapper(119, builder);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}

