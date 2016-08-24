package com.iGap.response;

import android.util.Log;

import com.iGap.G;
import com.iGap.proto.ProtoError;
import com.iGap.proto.ProtoResponse;
import com.iGap.proto.ProtoUserLogin;

public class UserLoginResponse extends MessageHandler {

    public int actionId;
    public Object message;

    public UserLoginResponse(int actionId, Object protoClass) {
        super(actionId, protoClass);

        this.message = protoClass;
        this.actionId = actionId;
    }


    @Override
    public void handler() {

        ProtoUserLogin.UserLoginResponse.Builder userLoginResponse = (ProtoUserLogin.UserLoginResponse.Builder) message;

        ProtoResponse.Response.Builder response = ProtoResponse.Response.newBuilder().mergeFrom(userLoginResponse.getResponse());
        Log.i("SOC", "userLoginResponse response.getId() : " + response.getId());
        Log.i("SOC", "userLoginResponse response.getTimestamp() : " + response.getTimestamp());

        G.onUserLogin.onLogin();
    }

    @Override
    public void timeOut() {
    }

    @Override
    public void error() {
        ProtoError.ErrorResponse.Builder errorResponse = (ProtoError.ErrorResponse.Builder) message;
        int majorCode = errorResponse.getMajorCode();
        int minorCode = errorResponse.getMinorCode();
    }
}


