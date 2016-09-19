package com.iGap.response;

import android.util.Log;

import com.iGap.G;

public class GroupAddMemberResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public GroupAddMemberResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }


    @Override
    public void handler() {

        G.onGroupAddMember.onGroupAddMember();

        Log.i("XXX", "GroupAddMemberResponse handler : " + message);
    }

    @Override
    public void error() {
        Log.i("XXX", "GroupAddMemberResponse Error : " + message);
    }
}
