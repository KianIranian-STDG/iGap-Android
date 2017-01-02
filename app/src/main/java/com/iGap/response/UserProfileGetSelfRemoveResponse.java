package com.iGap.response;

import com.iGap.G;
import com.iGap.proto.ProtoUserProfileGetSelfRemove;

public class UserProfileGetSelfRemoveResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public UserProfileGetSelfRemoveResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();
        ProtoUserProfileGetSelfRemove.UserProfileGetSelfRemoveResponse.Builder builder =
                (ProtoUserProfileGetSelfRemove.UserProfileGetSelfRemoveResponse.Builder) message;

        builder.getSelfRemove();

        if (G.onUserProfileGetSelfRemove != null) {
            G.onUserProfileGetSelfRemove.onUserSetSelfRemove(builder.getSelfRemove());
        }

    }

    @Override
    public void timeOut() {
        super.timeOut();
    }

    @Override
    public void error() {
        super.error();
    }
}


