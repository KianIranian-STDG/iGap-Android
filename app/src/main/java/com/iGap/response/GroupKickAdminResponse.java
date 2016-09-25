package com.iGap.response;

import android.util.Log;

import com.iGap.G;
import com.iGap.proto.ProtoGroupKickAdmin;

public class GroupKickAdminResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public GroupKickAdminResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }


    @Override
    public void handler() {

        ProtoGroupKickAdmin.GroupKickAdminResponse.Builder builder = (ProtoGroupKickAdmin.GroupKickAdminResponse.Builder) message;
        builder.getRoomId();
        builder.getMemberId();

        G.onGroupKickAdmin.onGroupKickAdmin(builder.getRoomId(), builder.getMemberId());

        Log.e("ddd", "hhhhhhhhhh      " + builder.getRoomId() + "   " + builder.getMemberId());

    }

    @Override
    public void error() {
        Log.e("ddd", "hhhhhhhhhh      erore      " + message);
    }

    @Override
    public void timeOut() {

        Log.e("ddd", "hhhhhhhhhh      timout      " + message);
        super.timeOut();
    }
}
