package com.iGap.response;

import android.util.Log;
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

    @Override public void handler() {

        Log.i("XXX", "UserProfileSetNicknameResponse handler : " + message);
        ProtoUserProfileNickname.UserProfileSetNicknameResponse.Builder
            userProfileNickNameResponse =
            (ProtoUserProfileNickname.UserProfileSetNicknameResponse.Builder) message;
        G.onUserProfileSetNickNameResponse.onUserProfileNickNameResponse(
            userProfileNickNameResponse.getNickname(), userProfileNickNameResponse.getResponse());
    }

    @Override public void timeOut() {
        Log.i("XXX", "UserProfileSetNicknameResponse timeOut");
    }

    @Override public void error() {
        ProtoError.ErrorResponse.Builder errorResponse = (ProtoError.ErrorResponse.Builder) message;
        final int majorCode = errorResponse.getMajorCode();
        final int minorCode = errorResponse.getMinorCode();

        Log.i("XXX", "UserRegisterResponse majorCode : " + majorCode);
        Log.i("XXX", "UserRegisterResponse minorCode : " + minorCode);

        Log.i("XXX", "UserProfileSetNicknameResponse error");

        G.onUserProfileSetNickNameResponse.onUserProfileNickNameError(majorCode, minorCode);
    }
}


