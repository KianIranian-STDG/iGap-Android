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

import net.iGap.G;
import net.iGap.proto.ProtoMplSetSalesResult;

public class RequestMplSetSalesResult {

    public interface OnSetSalesResult {
        void onSetResultReady();

        void onError(int major, int minor);
    }

    public boolean setSalesResult(String data, OnSetSalesResult onSetSalesResult) {
        ProtoMplSetSalesResult.MplSetSalesResult.Builder builder = ProtoMplSetSalesResult.MplSetSalesResult.newBuilder();
        builder.setData(data);

        RequestWrapper requestWrapper = new RequestWrapper(9103, builder, onSetSalesResult);
        try {
            if (G.userLogin) {
                RequestQueue.sendRequest(requestWrapper);
                return true;
            } else {
                return false;
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return false;
        }
    }
}
