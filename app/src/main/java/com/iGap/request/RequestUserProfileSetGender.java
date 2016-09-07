package com.iGap.request;

import com.iGap.proto.ProtoGlobal;
import com.iGap.proto.ProtoUserProfileGender;

public class RequestUserProfileSetGender {

    public void setUserProfileGender(ProtoGlobal.Gender gender) {

        ProtoUserProfileGender.UserProfileSetGender.Builder userProfileGender = ProtoUserProfileGender.UserProfileSetGender.newBuilder();
        userProfileGender.setGender(gender);

        RequestWrapper requestWrapper = new RequestWrapper(104, userProfileGender);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

}
