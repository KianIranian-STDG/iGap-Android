package com.iGap.response;

import com.iGap.G;
import com.iGap.proto.ProtoChannelCheckUsername;
import com.iGap.proto.ProtoError;

public class ChannelCheckUsernameResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public ChannelCheckUsernameResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();

        final ProtoChannelCheckUsername.ChannelCheckUsernameResponse.Builder builder = (ProtoChannelCheckUsername.ChannelCheckUsernameResponse.Builder) message;
        if (G.onChannelCheckUsername != null) {
            G.onChannelCheckUsername.onChannelCheckUsername(builder.getStatus());
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
        if (G.onChannelCheckUsername != null) {
            G.onChannelCheckUsername.onError(errorResponse.getMajorCode(), errorResponse.getMinorCode());
        }
    }
}


