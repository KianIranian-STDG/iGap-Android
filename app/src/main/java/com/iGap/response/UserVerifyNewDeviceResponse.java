package com.iGap.response;

import com.iGap.proto.ProtoUserVerifyNewDevice;

public class UserVerifyNewDeviceResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public UserVerifyNewDeviceResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();

        ProtoUserVerifyNewDevice.UserVerifyNewDeviceResponse.Builder builder = (ProtoUserVerifyNewDevice.UserVerifyNewDeviceResponse.Builder) message;
        builder.getAppName();
        builder.getAppId();
        builder.getAppBuildVersion();
        builder.getAppVersion();
        builder.getPlatform();
        builder.getPlatformVersion();
        builder.getDevice();
        builder.getDeviceName();
        builder.getTwoStepVerification();
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


