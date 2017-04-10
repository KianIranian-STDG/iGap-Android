package com.iGap.response;

import com.iGap.proto.ProtoQrCodeAddContact;

public class QrCodeAddContactResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public QrCodeAddContactResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();

        ProtoQrCodeAddContact.QrCodeAddContactResponse.Builder builder = (ProtoQrCodeAddContact.QrCodeAddContactResponse.Builder) message;
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


