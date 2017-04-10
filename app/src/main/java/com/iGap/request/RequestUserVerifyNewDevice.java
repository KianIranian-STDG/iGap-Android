package com.iGap.request;

import com.iGap.proto.ProtoUserVerifyNewDevice;

public class RequestUserVerifyNewDevice {

    public void verifyNewDevice(String token) {
        ProtoUserVerifyNewDevice.UserVerifyNewDevice.Builder builder = ProtoUserVerifyNewDevice.UserVerifyNewDevice.newBuilder();
        builder.setToken(token);

        RequestWrapper requestWrapper = new RequestWrapper(145, builder);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
