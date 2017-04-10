package com.iGap.request;

import com.iGap.proto.ProtoUserTwoStepVerificationRecoverPasswordByToken;

public class RequestUserTwoStepVerificationRecoverPasswordByToken {

    public void recoveryPasswordByToken(String token) {

        ProtoUserTwoStepVerificationRecoverPasswordByToken.UserTwoStepVerificationRecoverPasswordByToken.Builder builder = ProtoUserTwoStepVerificationRecoverPasswordByToken.UserTwoStepVerificationRecoverPasswordByToken.newBuilder();
        builder.setToken(token);

        RequestWrapper requestWrapper = new RequestWrapper(139, builder);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
