package com.iGap.response;

import com.iGap.G;
import com.iGap.proto.ProtoChannelAddAdmin;
import com.iGap.proto.ProtoError;

public class ChannelAddAdminResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public ChannelAddAdminResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();

        ProtoChannelAddAdmin.ChannelAddAdminResponse.Builder builder = (ProtoChannelAddAdmin.ChannelAddAdminResponse.Builder) message;
        if (G.onChannelAddAdmin != null) {
            G.onChannelAddAdmin.onChannelAddAdmin(builder.getRoomId(), builder.getMemberId());
        }
    }

    @Override
    public void timeOut() {
        super.timeOut();

        if (G.onChannelAddAdmin != null) {
            G.onChannelAddAdmin.onTimeOut();
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


