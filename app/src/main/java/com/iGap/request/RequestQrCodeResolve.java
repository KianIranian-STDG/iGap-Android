package com.iGap.request;

import com.iGap.proto.ProtoQrCodeResolve;

public class RequestQrCodeResolve {

    public void qrCodeResolve(String username, long messageId) {

        ProtoQrCodeResolve.QrCodeResolve.Builder builder = ProtoQrCodeResolve.QrCodeResolve.newBuilder();
        builder.setUsername(username);
        builder.setMessageId(messageId);

        RequestWrapper requestWrapper = new RequestWrapper(801, builder);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
