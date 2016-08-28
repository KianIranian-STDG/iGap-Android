package com.iGap.request;

import com.iGap.helper.HelperString;
import com.iGap.proto.ProtoGlobal;
import com.iGap.proto.ProtoRequest;
import com.iGap.proto.ProtoUserProfileGender;

public class RequestUserProfileGender {

    public void setUserProfileGender(ProtoGlobal.Gender gender) {

        ProtoUserProfileGender.UserProfileGender.Builder userProfileGender = ProtoUserProfileGender.UserProfileGender.newBuilder();
        userProfileGender.setRequest(ProtoRequest.Request.newBuilder().setId(HelperString.generateKey()));
        userProfileGender.setGender(gender);

        RequestWrapper requestWrapper = new RequestWrapper(104, userProfileGender);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

}
