package com.iGap.response;

import android.util.Log;
import com.iGap.G;
import com.iGap.proto.ProtoUserAvatarDelete;

public class UserAvatarDeleteResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public UserAvatarDeleteResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);
        this.message = protoClass;
        this.identity = identity;
        this.actionId = actionId;
    }

    @Override public void handler() {
        super.handler();

        ProtoUserAvatarDelete.UserAvatarDeleteResponse.Builder userAvatarDeleteResponse =
            (ProtoUserAvatarDelete.UserAvatarDeleteResponse.Builder) message;
        Log.i("XXX", "userAvatarDeleteResponse.getId() 2 : " + userAvatarDeleteResponse.getId());
        Log.i("XXX", "userAvatarDeleteResponse.identity  2 : " + identity);
        G.onUserAvatarDelete.onUserAvatarDelete(userAvatarDeleteResponse.getId(), identity);
    }

    @Override public void timeOut() {
        super.timeOut();
    }

    @Override public void error() {
        super.error();
    }
}


