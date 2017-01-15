package com.iGap.response;

import com.iGap.G;
import com.iGap.proto.ProtoError;
import com.iGap.proto.ProtoUserProfileNickname;

public class UserProfileSetNicknameResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public UserProfileSetNicknameResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();

        ProtoUserProfileNickname.UserProfileSetNicknameResponse.Builder userProfileNickNameResponse = (ProtoUserProfileNickname.UserProfileSetNicknameResponse.Builder) message;
        G.onUserProfileSetNickNameResponse.onUserProfileNickNameResponse(userProfileNickNameResponse.getNickname(), userProfileNickNameResponse.getResponse());
    }

    @Override
    public void timeOut() {
        super.timeOut();
    }

    @Override
    public void error() {
        super.error();
        ProtoError.ErrorResponse.Builder errorResponse = (ProtoError.ErrorResponse.Builder) message;
        final int majorCode = errorResponse.getMajorCode();
        final int minorCode = errorResponse.getMinorCode();

        G.onUserProfileSetNickNameResponse.onUserProfileNickNameError(majorCode, minorCode);
    }
}


