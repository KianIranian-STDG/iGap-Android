package com.iGap.response;

import android.util.Log;

import com.iGap.G;
import com.iGap.WebSocketClient;
import com.iGap.proto.ProtoError;

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
        Log.i("FFF", "handler 1");
        G.userLogin = true;
        WebSocketClient.waitingForReconnecting = false;
        WebSocketClient.allowForReconnecting = true;
        G.onUserLogin.onLogin();
    }

    @Override
    public void timeOut() {
        Log.i("FFF", "timeOut");
    }

    @Override
    public void error() {
        ProtoError.ErrorResponse.Builder errorResponse = (ProtoError.ErrorResponse.Builder) message;
        int majorCode = errorResponse.getMajorCode();
        int minorCode = errorResponse.getMinorCode();

        Log.i("FFF", "userLoginResponse response.majorCode() : " + majorCode);
        Log.i("FFF", "userLoginResponse response.minorCode() : " + minorCode);
        G.onUserLogin.onLoginError(majorCode, minorCode);
    }
}


