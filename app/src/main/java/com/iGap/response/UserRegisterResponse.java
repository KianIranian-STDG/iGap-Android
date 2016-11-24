package com.iGap.response;

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

    @Override
    public void handler() {
        super.handler();
        ProtoUserRegister.UserRegisterResponse.Builder builder =
                (ProtoUserRegister.UserRegisterResponse.Builder) message;
        G.onUserRegistration.onRegister(builder.getUsername(), builder.getUserId(),
                builder.getMethod(), builder.getSmsNumberList(), builder.getVerifyCodeRegex(),
                builder.getVerifyCodeDigitCount());
    }

    @Override
    public void error() {
        super.error();
        ProtoError.ErrorResponse.Builder errorResponse = (ProtoError.ErrorResponse.Builder) message;
        final int majorCode = errorResponse.getMajorCode();
        final int minorCode = errorResponse.getMinorCode();

        G.onUserRegistration.onRegisterError(majorCode, minorCode);
    }
}
