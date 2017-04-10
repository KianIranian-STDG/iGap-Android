package com.iGap.request;

import com.iGap.proto.ProtoUserTwoStepVerificationRecoverPasswordByAnswers;

public class RequestUserTwoStepVerificationRecoverPasswordByAnswers {

    public void RecoveryPasswordByAnswer(String answerOne, String answerTwo) {

        ProtoUserTwoStepVerificationRecoverPasswordByAnswers.UserTwoStepVerificationRecoverPasswordByAnswers.Builder builder = ProtoUserTwoStepVerificationRecoverPasswordByAnswers.UserTwoStepVerificationRecoverPasswordByAnswers.newBuilder();
        builder.setAnswerOne(answerOne);
        builder.setAnswerTwo(answerTwo);

        RequestWrapper requestWrapper = new RequestWrapper(140, builder);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
