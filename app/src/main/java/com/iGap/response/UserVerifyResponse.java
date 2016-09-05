package com.iGap.response;

import com.iGap.G;
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
        ProtoUserVerify.UserVerifyResponse.Builder userVerifyResponse = (ProtoUserVerify.UserVerifyResponse.Builder) message;
        G.onUserVerification.onUserVerify(userVerifyResponse.getToken(), userVerifyResponse.getNewUser());
    }

    @Override
    public void timeOut() {
    }

    @Override
    public void error() {
        G.onUserVerification.onUserVerifyError();
    }
}
