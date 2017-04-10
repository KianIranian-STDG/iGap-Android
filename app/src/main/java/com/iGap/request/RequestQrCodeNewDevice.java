package com.iGap.request;

import com.iGap.proto.ProtoGlobal;
import com.iGap.proto.ProtoQrCodeNewDevice;

public class RequestQrCodeNewDevice {

    public void qrCodeNewDevice(String appName, int appId, int appBuildVersion, String appVersion, ProtoGlobal.Platform platform, String platformVersion, ProtoGlobal.Device device, String deviceName) {

        ProtoQrCodeNewDevice.QrCodeNewDevice.Builder builder = ProtoQrCodeNewDevice.QrCodeNewDevice.newBuilder();
        builder.setAppName(appName);
        builder.setAppId(appId);
        builder.setAppBuildVersion(appBuildVersion);
        builder.setAppVersion(appVersion);
        builder.setPlatform(platform);
        builder.setPlatformVersion(platformVersion);
        builder.setDevice(device);
        builder.setDeviceName(deviceName);

        RequestWrapper requestWrapper = new RequestWrapper(802, builder);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
