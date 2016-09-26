package com.iGap.response;

import android.util.Log;

import com.iGap.G;
import com.iGap.proto.ProtoError;
import com.iGap.proto.ProtoUserLogin;

public class UserLoginResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public UserLoginResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.identity = identity;
        this.actionId = actionId;
    }


    @Override
    public void handler() {
        Log.i("SOC", "handler 1");
        ProtoUserLogin.UserLoginResponse.Builder userLoginResponse = (ProtoUserLogin.UserLoginResponse.Builder) message;
        Log.i("SOC", "handler 2");
        G.userLogin = true;
        G.onUserLogin.onLogin();
    }

    @Override
    public void timeOut() {
        Log.i("SOC", "timeOut");
    }

    @Override
    public void error() {
        ProtoError.ErrorResponse.Builder errorResponse = (ProtoError.ErrorResponse.Builder) message;
        int majorCode = errorResponse.getMajorCode();
        int minorCode = errorResponse.getMinorCode();

        Log.i("SOC", "userLoginResponse response.majorCode() : " + majorCode);
        Log.i("SOC", "userLoginResponse response.minorCode() : " + minorCode);
        G.onUserLogin.onLoginError(majorCode, minorCode);
    }
}


