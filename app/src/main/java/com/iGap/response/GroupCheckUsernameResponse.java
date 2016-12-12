package com.iGap.response;

import com.iGap.G;
import com.iGap.proto.ProtoError;
import com.iGap.proto.ProtoGroupCheckUsername;

public class GroupCheckUsernameResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public GroupCheckUsernameResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();

        final ProtoGroupCheckUsername.GroupCheckUsernameResponse.Builder builder = (ProtoGroupCheckUsername.GroupCheckUsernameResponse.Builder) message;
        if (G.onGroupCheckUsername != null) {
            G.onGroupCheckUsername.onGroupCheckUsername(builder.getStatus());
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
        if (G.onGroupCheckUsername != null) {
            G.onGroupCheckUsername.onError(errorResponse.getMajorCode(), errorResponse.getMinorCode());
        }
    }
}


