package com.iGap.request;

import com.iGap.proto.ProtoQrCodeJoin;

public class RequestQrCodeJoin {

    public void qrCodeJoin(String inviteToken) {

        ProtoQrCodeJoin.QrCodeJoin.Builder builder = ProtoQrCodeJoin.QrCodeJoin.newBuilder();
        builder.setInviteToken(inviteToken);

        RequestWrapper requestWrapper = new RequestWrapper(800, builder);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
