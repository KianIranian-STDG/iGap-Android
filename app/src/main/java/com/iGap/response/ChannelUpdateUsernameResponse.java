package com.iGap.response;

import com.iGap.G;
import com.iGap.proto.ProtoChannelUpdateUsername;
import com.iGap.proto.ProtoError;

public class ChannelUpdateUsernameResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public ChannelUpdateUsernameResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();

        ProtoChannelUpdateUsername.ChannelUpdateUsernameResponse.Builder builder = (ProtoChannelUpdateUsername.ChannelUpdateUsernameResponse.Builder) message;
        if (G.onChannelUpdateUsername != null) {
            G.onChannelUpdateUsername.onChannelUpdateUsername(builder.getRoomId(), builder.getUsername());
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
        if (G.onChannelUpdateUsername != null) {
            G.onChannelUpdateUsername.onError(errorResponse.getMajorCode(), errorResponse.getMinorCode());
        }
    }
}


