package com.iGap.request;

import com.iGap.proto.ProtoUserTwoStepVerificationChangeRecoveryQuestion;

public class RequestUserTwoStepVerificationChangeRecoveryQuestion {

    public void changeRecoveryQuestion(String password, String questinoOne, String answerOne, String questionTwo, String answerTwo) {

        ProtoUserTwoStepVerificationChangeRecoveryQuestion.UserTwoStepVerificationChangeRecoveryQuestion.Builder builder = ProtoUserTwoStepVerificationChangeRecoveryQuestion.UserTwoStepVerificationChangeRecoveryQuestion.newBuilder();
        builder.setPassword(password);
        builder.setQuestionOne(questinoOne);
        builder.setAnswerOne(answerOne);
        builder.setQuestionTwo(questionTwo);
        builder.setAnswerTwo(answerTwo);

        RequestWrapper requestWrapper = new RequestWrapper(141, builder);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
