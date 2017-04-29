/*
* This is the source code of iGap for Android
* It is licensed under GNU AGPL v3.0
* You should have received a copy of the license in this archive (see LICENSE).
* Copyright © 2017 , iGap - www.iGap.net
* iGap Messenger | Free, Fast and Secure instant messaging application
* The idea of the RooyeKhat Media Company - www.RooyeKhat.co
* All rights reserved.
*/

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

        RequestWrapper requestWrapper = new RequestWrapper(30133, builder);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
