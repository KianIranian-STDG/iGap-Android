/*
 * This is the source code of iGap for Android
 * It is licensed under GNU AGPL v3.0
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright © 2017 , iGap - www.iGap.net
 * iGap Messenger | Free, Fast and Secure instant messaging application
 * The idea of the Kianiranian Company - www.kianiranian.com
 * All rights reserved.
 */

package net.iGap.request;

import net.iGap.proto.ProtoMplGetBillToken;

public class RequestMplGetBillToken {

    public void mplGetBillToken(long billId, long payId, int type) {
        ProtoMplGetBillToken.MplGetBillToken.Builder builder = ProtoMplGetBillToken.MplGetBillToken.newBuilder();
        builder.setBillId(billId);
        builder.setPayId(payId);
        builder.setType(ProtoMplGetBillToken.MplGetBillToken.Type.forNumber(type));

        RequestWrapper requestWrapper = new RequestWrapper(9100, builder);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
