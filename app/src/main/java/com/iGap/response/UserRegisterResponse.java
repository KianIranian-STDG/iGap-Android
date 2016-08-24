package com.iGap.response;

import android.util.Log;

import com.iGap.G;
import com.iGap.proto.ProtoError;
import com.iGap.proto.ProtoResponse;
import com.iGap.proto.ProtoUserRegister;

public class UserRegisterResponse extends MessageHandler {

    public int actionId;
    public Object message;

    public UserRegisterResponse(int actionId, Object protoClass) {
        super(actionId, protoClass);

        this.message = protoClass;
        this.actionId = actionId;
    }


    @Override
    public void handler() {
        Log.i("SOC_RES", "UserRegisterResponse handler");

        ProtoUserRegister.UserRegisterResponse.Builder builder = (ProtoUserRegister.UserRegisterResponse.Builder) message;

        ProtoResponse.Response.Builder response = ProtoResponse.Response.newBuilder().mergeFrom(builder.getResponse());
        Log.i("SOC_RES", "UserRegisterResponse response.getId() : " + response.getId());
        Log.i("SOC_RES", "UserRegisterResponse response.getTimestamp() : " + response.getTimestamp());

        Log.i("SOC_RES", "UserRegisterResponse getUserId : " + builder.getUserId());
        Log.i("SOC_RES", "UserRegisterResponse getUsername : " + builder.getUsername());


        G.onUserRegistration.onRegister(builder.getUsername(), builder.getUserId(), builder.getMethod());


    }

    @Override
    public void error() {
        Log.i("SOC_RES", "UserRegisterResponse error");

        ProtoError.ErrorResponse.Builder errorResponse = (ProtoError.ErrorResponse.Builder) message;
        int majorCode = errorResponse.getMajorCode();
        int minorCode = errorResponse.getMinorCode();

        Log.i("SOC_RES", "UserRegisterResponse majorCode : " + majorCode);
        Log.i("SOC_RES", "UserRegisterResponse minorCode : " + minorCode);


    }
}
