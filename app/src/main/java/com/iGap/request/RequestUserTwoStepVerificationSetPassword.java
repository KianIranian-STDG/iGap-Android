package com.iGap.request;

import com.iGap.proto.ProtoUserTwoStepVerificationSetPassword;

public class RequestUserTwoStepVerificationSetPassword {

    public void setPassword(String oldPassword, String newPassword, String recoveryEmail, String questionOne, String answerOne, String questionTwo, String answerTwo, String hint) {
        ProtoUserTwoStepVerificationSetPassword.UserTwoStepVerificationSetPassword.Builder builder = ProtoUserTwoStepVerificationSetPassword.UserTwoStepVerificationSetPassword.newBuilder();
        builder.setOldPassword(oldPassword);
        builder.setNewPassword(newPassword);
        builder.setRecoveryEmail(recoveryEmail);
        builder.setQuestionOne(questionOne);
        builder.setAnswerOne(answerOne);
        builder.setQuestionTwo(questionTwo);
        builder.setAnswerTwo(answerTwo);
        builder.setHint(hint);

        RequestWrapper requestWrapper = new RequestWrapper(133, builder);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
