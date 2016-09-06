package com.iGap.response;

import android.util.Log;

import com.iGap.G;
import com.iGap.proto.ProtoError;
import com.iGap.proto.ProtoUserProfileNickname;

public class UserProfileNicknameResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public UserProfileNicknameResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }


    @Override
    public void handler() {

        ProtoUserProfileNickname.UserProfileNicknameResponse.Builder userProfileNickNameResponse = (ProtoUserProfileNickname.UserProfileNicknameResponse.Builder) message;
        G.onUserProfileNickNameResponse.onUserProfileNickNameResponse(userProfileNickNameResponse.getNickname(), userProfileNickNameResponse.getResponse());

    }

    @Override
    public void timeOut() {
        Log.i("XXX", "UserProfileNicknameResponse timeOut");
    }

    @Override
    public void error() {
        ProtoError.ErrorResponse.Builder errorResponse = (ProtoError.ErrorResponse.Builder) message;
        final int majorCode = errorResponse.getMajorCode();
        final int minorCode = errorResponse.getMinorCode();

        Log.i("XXX", "UserRegisterResponse majorCode : " + majorCode);
        Log.i("XXX", "UserRegisterResponse minorCode : " + minorCode);

        Log.i("XXX", "UserProfileNicknameResponse error");
    }
}


