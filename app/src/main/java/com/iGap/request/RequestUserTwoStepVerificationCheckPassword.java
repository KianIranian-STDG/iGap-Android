package com.iGap.request;

import com.iGap.proto.ProtoUserTwoStepVerificationCheckPassword;

public class RequestUserTwoStepVerificationCheckPassword {

    public void checkPassword(String password) {
        ProtoUserTwoStepVerificationCheckPassword.UserTwoStepVerificationCheckPassword.Builder builder = ProtoUserTwoStepVerificationCheckPassword.UserTwoStepVerificationCheckPassword.newBuilder();
        builder.setPassword(password);

        RequestWrapper requestWrapper = new RequestWrapper(135, builder);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
