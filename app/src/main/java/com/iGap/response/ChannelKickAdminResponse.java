package com.iGap.response;

import com.iGap.G;
import com.iGap.proto.ProtoChannelKickAdmin;

public class ChannelKickAdminResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public ChannelKickAdminResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();

        ProtoChannelKickAdmin.ChannelKickAdminResponse.Builder builder = (ProtoChannelKickAdmin.ChannelKickAdminResponse.Builder) message;
        if (G.onChannelKickAdmin != null) {
            G.onChannelKickAdmin.onChannelKickAdmin(builder.getRoomId(), builder.getMemberId());
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


