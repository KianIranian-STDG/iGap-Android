package com.iGap.response;

import com.iGap.G;
import com.iGap.proto.ProtoError;
import com.iGap.proto.ProtoGroupUpdateUsername;

public class GroupUpdateUsernameResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public GroupUpdateUsernameResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();

        ProtoGroupUpdateUsername.GroupUpdateUsernameResponse.Builder builder = (ProtoGroupUpdateUsername.GroupUpdateUsernameResponse.Builder) message;
        if (G.onGroupUpdateUsername != null) {
            G.onGroupUpdateUsername.onGroupUpdateUsername(builder.getRoomId(), builder.getUsername());
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
        if (G.onGroupUpdateUsername != null) {
            G.onGroupUpdateUsername.onError(errorResponse.getMajorCode(), errorResponse.getMinorCode());
        }
    }
}


