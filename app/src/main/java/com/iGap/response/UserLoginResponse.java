package com.iGap.response;

import com.iGap.G;
import com.iGap.WebSocketClient;
import com.iGap.helper.HelperLogout;
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
        super.handler();
        G.userLogin = true;
        WebSocketClient.waitingForReconnecting = false;
        WebSocketClient.allowForReconnecting = true;
        G.onUserLogin.onLogin();
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

        if (majorCode == 111 && minorCode != 4) {
            HelperLogout.logout();
            return;
        }

        G.onUserLogin.onLoginError(majorCode, minorCode);
    }
}


