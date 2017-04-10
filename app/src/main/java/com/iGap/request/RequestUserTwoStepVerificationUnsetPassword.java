package com.iGap.request;

import com.iGap.proto.ProtoUserTwoStepVerificationUnsetPassword;

public class RequestUserTwoStepVerificationUnsetPassword {

    public void unsetPassword(String password) {

        ProtoUserTwoStepVerificationUnsetPassword.UserTwoStepVerificationUnsetPassword.Builder builder = ProtoUserTwoStepVerificationUnsetPassword.UserTwoStepVerificationUnsetPassword.newBuilder();
        builder.setPassword(password);

        RequestWrapper requestWrapper = new RequestWrapper(134, builder);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
