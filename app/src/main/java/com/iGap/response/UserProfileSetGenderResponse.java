package com.iGap.response;

import android.util.Log;

import com.iGap.G;
import com.iGap.proto.ProtoError;
import com.iGap.proto.ProtoUserProfileGender;

public class UserProfileSetGenderResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public UserProfileSetGenderResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();
        Log.i("XXX", "UserProfileSetGenderResponse message : " + message);
        ProtoUserProfileGender.UserProfileSetGenderResponse.Builder builder = (ProtoUserProfileGender.UserProfileSetGenderResponse.Builder) message;
        G.onUserProfileSetGenderResponse.onUserProfileEmailResponse(builder.getGender(), builder.getResponse());
    }

    @Override
    public void timeOut() {
        super.timeOut();
    }

    @Override
    public void error() {

        ProtoError.ErrorResponse.Builder errorResponse = (ProtoError.ErrorResponse.Builder) message;
        int majorCode = errorResponse.getMajorCode();
        int minorCode = errorResponse.getMinorCode();

        Log.i("XXX", "UserProfileSetGenderResponse response.majorCode() : " + majorCode);
        Log.i("XXX", "UserProfileSetGenderResponse response.minorCode() : " + minorCode);
        G.onUserProfileSetGenderResponse.Error(majorCode, minorCode);

    }
}


