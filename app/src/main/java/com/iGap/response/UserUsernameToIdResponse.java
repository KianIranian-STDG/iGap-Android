package com.iGap.response;

import com.iGap.proto.ProtoUserUsernameToId;

public class UserUsernameToIdResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public UserUsernameToIdResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.actionId = actionId;
        this.message = protoClass;
        this.identity = identity;
    }

    @Override public void handler() {
        ProtoUserUsernameToId.UserUsernameToIdResponse.Builder builder =
            (ProtoUserUsernameToId.UserUsernameToIdResponse.Builder) message;
        builder.getUserId();
    }

    @Override public void timeOut() {
    }

    @Override public void error() {
    }
}


