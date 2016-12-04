package com.iGap.response;

import com.iGap.G;
import com.iGap.proto.ProtoChannelKickMember;

public class ChannelKickMemberResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public ChannelKickMemberResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();

        ProtoChannelKickMember.ChannelKickMemberResponse.Builder builder = (ProtoChannelKickMember.ChannelKickMemberResponse.Builder) message;
        if (G.onChannelKickMember != null) {
            G.onChannelKickMember.onChannelKickMember(builder.getRoomId(), builder.getMemberId());
        }
    }

    @Override
    public void timeOut() {
        super.timeOut();
    }

    @Override
    public void error() {
        super.error();
    }
}


