package com.iGap.response;

import com.iGap.proto.ProtoQrCodeAddMe;

public class QrCodeAddMeResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public QrCodeAddMeResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();

        ProtoQrCodeAddMe.QrCodeAddMeResponse.Builder builder = (ProtoQrCodeAddMe.QrCodeAddMeResponse.Builder) message;
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


