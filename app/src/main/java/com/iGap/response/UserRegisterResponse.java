package com.iGap.response;

import android.util.Log;
import android.widget.Toast;
import com.iGap.G;
import com.iGap.proto.ProtoError;
import com.iGap.proto.ProtoUserRegister;

public class UserRegisterResponse extends MessageHandler {

    public int actionId;
    public Object message;

    public UserRegisterResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override public void handler() {
        Log.i("SOC_RES", "UserRegisterResponse handler message : " + message);
        ProtoUserRegister.UserRegisterResponse.Builder builder =
            (ProtoUserRegister.UserRegisterResponse.Builder) message;
        Log.i("SOC_RES", "SOC_RES 1");
        G.onUserRegistration.onRegister(builder.getUsername(), builder.getUserId(),
            builder.getMethod(), builder.getSmsNumberList(), builder.getVerifyCodeRegex());
    }

    @Override public void error() {
        ProtoError.ErrorResponse.Builder errorResponse = (ProtoError.ErrorResponse.Builder) message;
        final int majorCode = errorResponse.getMajorCode();
        final int minorCode = errorResponse.getMinorCode();

        Log.i("SOC_RES", "UserRegisterResponse majorCode : " + majorCode);
        Log.i("SOC_RES", "UserRegisterResponse minorCode : " + minorCode);

        G.currentActivity.runOnUiThread(new Runnable() {
            @Override public void run() {
                Toast.makeText(G.context,
                    "Internal Error Major:" + majorCode + " and Minor:" + minorCode,
                    Toast.LENGTH_SHORT).show();
            }
        });

        G.onUserRegistration.onRegisterError(majorCode, minorCode);
    }
}
