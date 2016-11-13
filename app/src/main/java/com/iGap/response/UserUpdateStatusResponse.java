package com.iGap.response;

import android.util.Log;

import com.iGap.G;
import com.iGap.proto.ProtoUserUpdateStatus;

public class UserUpdateStatusResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public UserUpdateStatusResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);
        this.message = protoClass;
        this.identity = identity;
        this.actionId = actionId;
    }

    @Override
    public void handler() {
        super.handler();
        ProtoUserUpdateStatus.UserUpdateStatusResponse.Builder builder = (ProtoUserUpdateStatus.UserUpdateStatusResponse.Builder) message;
        //    G.onUserUpdateStatus.onUserUpdateStatus(builder.getUserId(), builder.getStatus());


        if (builder.getStatus() == ProtoUserUpdateStatus.UserUpdateStatus.Status.ONLINE) {
            G.isUserStatusOnline = true;
        } else {
            G.isUserStatusOnline = false;
        }


        Log.e("ddd", builder.getStatus() + "");
    }

    @Override
    public void timeOut() {
        super.timeOut();
        Log.e("ddd", "timeOut   UserUpdateStatusResponse ");
    }

    @Override
    public void error() {
        super.error();
        Log.e("ddd", "error   UserUpdateStatusResponse ");
    }
}


