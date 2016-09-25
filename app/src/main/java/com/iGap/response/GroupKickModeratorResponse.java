package com.iGap.response;

import com.iGap.G;
import com.iGap.proto.ProtoGroupKickModerator;

public class GroupKickModeratorResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public GroupKickModeratorResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }


    @Override
    public void handler() {

        ProtoGroupKickModerator.GroupKickModeratorResponse.Builder builder = (ProtoGroupKickModerator.GroupKickModeratorResponse.Builder) message;
        builder.getRoomId();
        builder.getMemberId();

        G.onGroupKickModerator.onGroupKickModerator(builder.getRoomId(), builder.getMemberId());

    }

    @Override
    public void error() {

    }
}
