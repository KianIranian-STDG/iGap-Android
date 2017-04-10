package com.iGap.response;

import com.iGap.proto.ProtoQrCodeNewDevice;

public class QrCodeNewDeviceResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public QrCodeNewDeviceResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();

        ProtoQrCodeNewDevice.QrCodeNewDeviceResponse.Builder builder = (ProtoQrCodeNewDevice.QrCodeNewDeviceResponse.Builder) message;
        builder.getQrCodeImage();
        builder.getExpireTime();
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


