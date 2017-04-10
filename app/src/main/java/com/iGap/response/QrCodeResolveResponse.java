package com.iGap.response;

import com.iGap.proto.ProtoQrCodeResolve;

public class QrCodeResolveResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public QrCodeResolveResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();

        ProtoQrCodeResolve.QrCodeResolveResponse.Builder builder = (ProtoQrCodeResolve.QrCodeResolveResponse.Builder) message;
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


