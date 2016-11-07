package com.iGap.response;

import android.util.Log;

import com.iGap.G;
import com.iGap.proto.ProtoError;
import com.iGap.proto.ProtoUserProfileCheckUsername;

public class UserProfileCheckUsernameResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public UserProfileCheckUsernameResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {

        ProtoUserProfileCheckUsername.UserProfileCheckUsernameResponse.Builder builder =
                (ProtoUserProfileCheckUsername.UserProfileCheckUsernameResponse.Builder) message;

        G.onUserProfileCheckUsername.OnUserProfileCheckUsername(builder.getStatus());
    }

    @Override
    public void timeOut() {
        super.timeOut();
    }

    @Override
    public void error() {
        super.error();

        ProtoError.ErrorResponse.Builder errorResponse = (ProtoError.ErrorResponse.Builder) message;
        int majorCode = errorResponse.getMajorCode();
        int minorCode = errorResponse.getMinorCode();

        Log.i("XXX", "UserProfileCheckUsernameResponse response.majorCode() : " + majorCode);
        Log.i("XXX", "UserProfileCheckUsernameResponse response.minorCode() : " + minorCode);
        G.onUserProfileCheckUsername.Error(majorCode, minorCode);

    }
}


