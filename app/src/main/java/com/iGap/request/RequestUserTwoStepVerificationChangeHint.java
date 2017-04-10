package com.iGap.request;

import com.iGap.proto.ProtoUserTwoStepVerificationChangeHint;

public class RequestUserTwoStepVerificationChangeHint {

    public void changeHint(String password, String hint) {
        ProtoUserTwoStepVerificationChangeHint.UserTwoStepVerificationChangeHint.Builder builder = ProtoUserTwoStepVerificationChangeHint.UserTwoStepVerificationChangeHint.newBuilder();
        builder.setPassword(password);
        builder.setHint(hint);

        RequestWrapper requestWrapper = new RequestWrapper(142, builder);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
