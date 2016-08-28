package com.iGap.response;

import com.iGap.G;
import com.iGap.proto.ProtoUserProfileNickname;

public class UserProfileNicknameResponse extends MessageHandler {

    public int actionId;
    public Object message;

    public UserProfileNicknameResponse(int actionId, Object protoClass) {
        super(actionId, protoClass);

        this.message = protoClass;
        this.actionId = actionId;
    }


    @Override
    public void handler() {

        ProtoUserProfileNickname.UserProfileNicknameResponse.Builder userProfileNickNameResponse = (ProtoUserProfileNickname.UserProfileNicknameResponse.Builder) message;
        G.onUserProfileNickNameResponse.onUserProfileNickNameResponse(userProfileNickNameResponse.getNickname(), userProfileNickNameResponse.getResponse());

    }

    @Override
    public void timeOut() {
    }

    @Override
    public void error() {
    }
}


