package com.iGap.response;

import com.iGap.G;
import com.iGap.proto.ProtoChannelAddMessageReaction;
import com.iGap.proto.ProtoError;

public class ChannelAddMessageReactionResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public ChannelAddMessageReactionResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();

        ProtoChannelAddMessageReaction.ChannelAddMessageReactionResponse.Builder builder = (ProtoChannelAddMessageReaction.ChannelAddMessageReactionResponse.Builder) message;
        if (G.onChannelAddMessageReaction != null) {
            G.onChannelAddMessageReaction.onChannelAddMessageReaction(builder.getReactionCounterLabel());
        }
    }

    @Override
    public void timeOut() {
        super.timeOut();
    }

    @Override
    public void error() {
        super.error();
        ProtoError.ErrorResponse.Builder errorResponse = (ProtoError.ErrorResponse.Builder) message;
        int majorCode = errorResponse.getMajorCode();
        int minorCode = errorResponse.getMinorCode();
        if (G.onChannelAddMessageReaction != null) {
            G.onChannelAddMessageReaction.onError(majorCode, minorCode);
        }
    }
}


