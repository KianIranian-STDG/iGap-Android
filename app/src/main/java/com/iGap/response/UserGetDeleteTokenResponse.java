package com.iGap.response;

import com.iGap.proto.ProtoUserGetDeleteToken;

public class UserGetDeleteTokenResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public UserGetDeleteTokenResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override public void handler() {

        final ProtoUserGetDeleteToken.UserGetDeleteTokenResponse.Builder builder =
            (ProtoUserGetDeleteToken.UserGetDeleteTokenResponse.Builder) message;

        builder.getResendDelay();
        builder.getSmsNumberList();
        builder.getTokenRegex();
        builder.getTokenLenght();
    }

    @Override public void timeOut() {
        super.timeOut();
    }

    @Override public void error() {
        super.error();
    }
}
