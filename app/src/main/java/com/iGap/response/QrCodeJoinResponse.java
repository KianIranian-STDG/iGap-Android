package com.iGap.response;

import com.iGap.proto.ProtoQrCodeJoin;

public class QrCodeJoinResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public QrCodeJoinResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();

        ProtoQrCodeJoin.QrCodeJoinResponse.Builder builder = (ProtoQrCodeJoin.QrCodeJoinResponse.Builder) message;
        builder.getQrCodeImage();
    }

    @Override
    public void timeOut() {
        super.timeOut();
    }

    @Override
    public void error() {
        super.error();
    }
}


