package com.iGap.request;

import com.iGap.proto.ProtoUserTwoStepVerificationRequestRecoveryToken;

public class RequestUserTwoStepVerificationRequestRecoveryToken {

    public void requestRecovertyToken() {
        ProtoUserTwoStepVerificationRequestRecoveryToken.UserTwoStepVerificationRequestRecoveryToken.Builder builder = ProtoUserTwoStepVerificationRequestRecoveryToken.UserTwoStepVerificationRequestRecoveryToken.newBuilder();

        RequestWrapper requestWrapper = new RequestWrapper(138, builder);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
