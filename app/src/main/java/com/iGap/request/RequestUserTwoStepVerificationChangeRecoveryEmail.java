package com.iGap.request;

import com.iGap.proto.ProtoUserTwoStepVerificationChangeRecoveryEmail;

public class RequestUserTwoStepVerificationChangeRecoveryEmail {

    public void changeRecoveryEmail(String password, String email) {

        ProtoUserTwoStepVerificationChangeRecoveryEmail.UserTwoStepVerificationChangeRecoveryEmail.Builder builder = ProtoUserTwoStepVerificationChangeRecoveryEmail.UserTwoStepVerificationChangeRecoveryEmail.newBuilder();
        builder.setPassword(password);
        builder.setEmail(email);

        RequestWrapper requestWrapper = new RequestWrapper(137, builder);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
