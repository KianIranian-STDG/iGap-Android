package com.iGap.response;

import android.util.Log;
import com.iGap.G;
import com.iGap.proto.ProtoError;
import com.iGap.proto.ProtoUserProfileEmail;

public class UserProfileSetEmailResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public UserProfileSetEmailResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override public void handler() {
        ProtoUserProfileEmail.UserProfileSetEmailResponse.Builder userProfileEmail =
            (ProtoUserProfileEmail.UserProfileSetEmailResponse.Builder) message;
        G.onUserProfileSetEmailResponse.onUserProfileEmailResponse(userProfileEmail.getEmail(),
            userProfileEmail.getResponse());
    }

    @Override public void timeOut() {
    }

    @Override public void error() {
        ProtoError.ErrorResponse.Builder errorResponse = (ProtoError.ErrorResponse.Builder) message;
        int majorCode = errorResponse.getMajorCode();
        int minorCode = errorResponse.getMinorCode();

        Log.i("XXX", "UserProfileSetEmailResponse response.majorCode() : " + majorCode);
        Log.i("XXX", "UserProfileSetEmailResponse response.minorCode() : " + minorCode);
        G.onUserProfileSetEmailResponse.Error(majorCode, minorCode);

    }
}


