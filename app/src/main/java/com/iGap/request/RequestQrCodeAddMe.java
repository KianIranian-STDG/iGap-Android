package com.iGap.request;

import com.iGap.proto.ProtoQrCodeAddMe;

public class RequestQrCodeAddMe {

    public void qrCodeAddMe() {

        ProtoQrCodeAddMe.QrCodeAddMe.Builder builder = ProtoQrCodeAddMe.QrCodeAddMe.newBuilder();

        RequestWrapper requestWrapper = new RequestWrapper(804, builder);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
