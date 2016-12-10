package com.iGap.response;

import com.iGap.G;
import com.iGap.helper.HelperRealmDelete;
import com.iGap.proto.ProtoChannelDelete;
import com.iGap.proto.ProtoError;

public class ChannelDeleteResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public ChannelDeleteResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();
        final ProtoChannelDelete.ChannelDeleteResponse.Builder builder = (ProtoChannelDelete.ChannelDeleteResponse.Builder) message;

        HelperRealmDelete.deleteRoom(builder.getRoomId());

        if (G.onChannelDelete != null) {
            G.onChannelDelete.onChannelDelete(builder.getRoomId());
        }
    }

    @Override
    public void timeOut() {
        super.timeOut();
        if (G.onChannelDelete != null) {
            G.onChannelDelete.onTimeOut();
        }
    }

    @Override
    public void error() {
        super.error();
        ProtoError.ErrorResponse.Builder errorResponse = (ProtoError.ErrorResponse.Builder) message;
        int majorCode = errorResponse.getMajorCode();
        int minorCode = errorResponse.getMinorCode();

        if (G.onChannelDelete != null) {
            G.onChannelDelete.onError(majorCode, minorCode);
        }
    }
}


