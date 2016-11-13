package com.iGap.request;

import android.util.Log;

import com.iGap.proto.ProtoUserUpdateStatus;

public class RequestUserUpdateStatus {

    public void userUpdateStatus(ProtoUserUpdateStatus.UserUpdateStatus.Status status) {

        ProtoUserUpdateStatus.UserUpdateStatus.Builder builder = ProtoUserUpdateStatus.UserUpdateStatus.newBuilder();
        builder.setStatus(status);

        Log.i("DDD", "RequestUserUpdateStatus : " + status);

        RequestWrapper requestWrapper = new RequestWrapper(124, builder);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}

