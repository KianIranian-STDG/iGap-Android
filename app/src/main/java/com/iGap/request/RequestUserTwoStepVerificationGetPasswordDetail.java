package com.iGap.request;

import com.iGap.proto.ProtoUserTwoStepVerificationGetPasswordDetail;

public class RequestUserTwoStepVerificationGetPasswordDetail {

    public void getPasswordDetail(String password) {
        ProtoUserTwoStepVerificationGetPasswordDetail.UserTwoStepVerificationGetPasswordDetail.Builder builder = ProtoUserTwoStepVerificationGetPasswordDetail.UserTwoStepVerificationGetPasswordDetail.newBuilder();

        RequestWrapper requestWrapper = new RequestWrapper(131, builder);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
