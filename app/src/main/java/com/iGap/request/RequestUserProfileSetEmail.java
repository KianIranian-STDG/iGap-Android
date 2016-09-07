package com.iGap.request;

import com.iGap.helper.HelperString;
import com.iGap.proto.ProtoRequest;
import com.iGap.proto.ProtoUserProfileEmail;

public class RequestUserProfileSetEmail {

    public void setUserProfileEmail(String email) {
        ProtoUserProfileEmail.UserProfileSetEmail.Builder userProfileEmail = ProtoUserProfileEmail.UserProfileSetEmail.newBuilder();
        userProfileEmail.setRequest(ProtoRequest.Request.newBuilder().setId(HelperString.generateKey()));
        userProfileEmail.setEmail(email);

        RequestWrapper requestWrapper = new RequestWrapper(103, userProfileEmail, null);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

}
