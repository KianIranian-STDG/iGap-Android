package com.iGap.response;

import android.util.Log;

import com.iGap.G;
import com.iGap.proto.ProtoGroupKickMember;

public class GroupKickMemberResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public GroupKickMemberResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }


    @Override
    public void handler() {

        ProtoGroupKickMember.GroupKickMemberResponse.Builder builder = (ProtoGroupKickMember.GroupKickMemberResponse.Builder) message;
        builder.getRoomId();
        builder.getMemberId();

        G.onGroupKickMember.onGroupKickMember(builder.getRoomId(), builder.getMemberId());
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
