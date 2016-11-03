package com.iGap.response;

import android.util.Log;
import com.iGap.G;
import com.iGap.proto.ProtoError;
import com.iGap.proto.ProtoUserProfileSetSelfRemove;

public class UserProfileSetSelfRemoveResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public UserProfileSetSelfRemoveResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override public void handler() {
        ProtoUserProfileSetSelfRemove.UserProfileSetSelfRemoveResponse.Builder builder = (ProtoUserProfileSetSelfRemove.UserProfileSetSelfRemoveResponse.Builder) message;

        builder.getSelfRemove();

        G.onUserProfileSetSelfRemove.onUserSetSelfRemove(builder.getSelfRemove());
    }

    @Override public void timeOut() {
        super.timeOut();
    }

    @Override public void error() {
        super.error();

        ProtoError.ErrorResponse.Builder errorResponse = (ProtoError.ErrorResponse.Builder) message;
        int majorCode = errorResponse.getMajorCode();
        int minorCode = errorResponse.getMinorCode();

        Log.i("XXX", "UserProfileSetSelfRemoveResponse response.majorCode() : " + majorCode);
        Log.i("XXX", "UserProfileSetSelfRemoveResponse response.minorCode() : " + minorCode);
        G.onUserProfileSetSelfRemove.Error(majorCode, minorCode);
    }
}


