package com.iGap.response;

import com.iGap.G;
import com.iGap.proto.ProtoChannelAddModerator;
import com.iGap.proto.ProtoError;

public class ChannelAddModeratorResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public ChannelAddModeratorResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();

        ProtoChannelAddModerator.ChannelAddModeratorResponse.Builder builder = (ProtoChannelAddModerator.ChannelAddModeratorResponse.Builder) message;
        if (G.onChannelAddModerator != null) {
            G.onChannelAddModerator.onChannelAddModerator(builder.getRoomId(), builder.getMemberId());
        }
    }

    @Override
    public void timeOut() {
        super.timeOut();
        if (G.onChannelAddModerator != null) {
            G.onChannelAddModerator.onTimeOut();
        }
    }

    @Override
    public void error() {
        super.error();

        ProtoError.ErrorResponse.Builder errorResponse = (ProtoError.ErrorResponse.Builder) message;
        int majorCode = errorResponse.getMajorCode();
        int minorCode = errorResponse.getMinorCode();

        if (G.onChannelAddAdmin != null) {
            G.onChannelAddAdmin.onError(majorCode, minorCode);
        }
    }
}


