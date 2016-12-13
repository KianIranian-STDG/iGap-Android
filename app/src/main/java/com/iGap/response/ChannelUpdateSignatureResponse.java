package com.iGap.response;

import com.iGap.G;
import com.iGap.proto.ProtoChannelUpdateSignature;
import com.iGap.proto.ProtoError;

public class ChannelUpdateSignatureResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public ChannelUpdateSignatureResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();

        ProtoChannelUpdateSignature.ChannelUpdateSignatureResponse.Builder builder = (ProtoChannelUpdateSignature.ChannelUpdateSignatureResponse.Builder) message;
        if (G.onChannelUpdateSignature != null) {
            G.onChannelUpdateSignature.onChannelUpdateSignatureResponse(builder.getRoomId(), builder.getSignature());
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
        if (G.onChannelUpdateSignature != null) {
            G.onChannelUpdateSignature.onError(majorCode, minorCode);
        }
    }
}


