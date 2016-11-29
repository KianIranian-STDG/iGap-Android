package com.iGap.response;

import android.util.Log;

import com.iGap.G;
import com.iGap.proto.ProtoChannelLeft;
import com.iGap.proto.ProtoError;

public class ChannelLeftResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public ChannelLeftResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();

        ProtoChannelLeft.ChannelLeftResponse.Builder builder = (ProtoChannelLeft.ChannelLeftResponse.Builder) message;
        builder.getRoomId();
        builder.getMemberId();
        Log.i("GGGGGG", "ChannelLeftResponse handler: ");
        if (G.onChannelLeft != null) {

            G.onChannelLeft.onChannelLeft(builder.getRoomId(), builder.getMemberId());

        }
    }

    @Override
    public void timeOut() {
        super.timeOut();
        Log.i("GGGGGG", "ChannelLeftResponse timeOut: ");
        if (G.onChannelLeft != null) {

            G.onChannelLeft.onTimeOut();

        }
    }

    @Override
    public void error() {
        super.error();

        Log.i("GGGGGG", "ChannelLeftResponse error: ");
        ProtoError.ErrorResponse.Builder errorResponse = (ProtoError.ErrorResponse.Builder) message;
        int majorCode = errorResponse.getMajorCode();
        int minorCode = errorResponse.getMinorCode();

        if (G.onChannelLeft != null) {
            G.onChannelLeft.onError(majorCode, minorCode);
        }

        Log.i("GGGGGG", "ChannelLeftResponse minorCode: " + minorCode);
        Log.i("GGGGGG", "ChannelLeftResponse majorCode: " + majorCode);
    }
}


