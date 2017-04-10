package com.iGap.request;

import com.iGap.proto.ProtoUserTwoStepVerificationVerifyRecoveryEmail;

public class RequestUserTwoStepVerificationVerifyRecoveryEmail {

    public void recoveryEmail(String token) {

        ProtoUserTwoStepVerificationVerifyRecoveryEmail.UserTwoStepVerificationVerifyRecoveryEmail.Builder builder = ProtoUserTwoStepVerificationVerifyRecoveryEmail.UserTwoStepVerificationVerifyRecoveryEmail.newBuilder();
        builder.setToken(token);

        RequestWrapper requestWrapper = new RequestWrapper(136, builder);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
