package com.iGap.request;

import com.iGap.proto.ProtoUserTwoStepVerificationVerifyPassword;

public class RequestUserTwoStepVerificationVerifyPassword {

    public void verifyPassword(String password) {
        ProtoUserTwoStepVerificationVerifyPassword.UserTwoStepVerificationVerifyPassword.Builder builder = ProtoUserTwoStepVerificationVerifyPassword.UserTwoStepVerificationVerifyPassword.newBuilder();
        builder.setPassword(password);

        RequestWrapper requestWrapper = new RequestWrapper(132, builder);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
