package com.iGap.response;

import com.iGap.G;
import com.iGap.proto.ProtoUserProfileGetNickname;

public class UserProfileGetNicknameResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public UserProfileGetNicknameResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.actionId = actionId;
        this.message = protoClass;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();
        ProtoUserProfileGetNickname.UserProfileGetNicknameResponse.Builder builder = (ProtoUserProfileGetNickname.UserProfileGetNicknameResponse.Builder) message;
        G.onUserProfileGetNickname.onUserProfileGetNickname(builder.getNickname());
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


