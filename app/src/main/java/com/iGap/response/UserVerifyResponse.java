package com.iGap.response;

import android.util.Log;

import com.iGap.G;
import com.iGap.proto.ProtoError;
import com.iGap.proto.ProtoUserVerify;

public class UserVerifyResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public UserVerifyResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        ProtoUserVerify.UserVerifyResponse.Builder userVerifyResponse =
                (ProtoUserVerify.UserVerifyResponse.Builder) message;
        G.onUserVerification.onUserVerify(userVerifyResponse.getToken(),
                userVerifyResponse.getNewUser());
    }

    @Override
    public void timeOut() {
    }

    @Override
    public void error() {
        ProtoError.ErrorResponse.Builder errorResponse = (ProtoError.ErrorResponse.Builder) message;
        int majorCode = errorResponse.getMajorCode();
        int minorCode = errorResponse.getMinorCode();

        Log.i("SOC_RES", "UserVerifyResponse majorCode : " + majorCode);
        Log.i("SOC_RES", "UserVerifyResponse minorCode : " + minorCode);

        G.onUserVerification.onUserVerifyError(majorCode, minorCode);
    }
}
