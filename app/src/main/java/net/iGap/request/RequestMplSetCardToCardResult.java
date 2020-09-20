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

import net.iGap.helper.RequestManager;
import net.iGap.module.accountManager.AccountManager;
import net.iGap.proto.ProtoMplSetCardToCardResult;

public class RequestMplSetCardToCardResult {

    public interface OnSetCardToCardResult {
        void onSetResultCardToCardReady();

        void onError(int major, int minor);
    }

    public boolean onSetCardToCardResult(String data, long toUserId, OnSetCardToCardResult onSetCardToCardResult) {
        ProtoMplSetCardToCardResult.MplSetCardToCardResult.Builder builder = ProtoMplSetCardToCardResult.MplSetCardToCardResult.newBuilder();
        builder.setData(data);
        builder.setToUserId(toUserId);

        RequestWrapper requestWrapper = new RequestWrapper(9108, builder, onSetCardToCardResult);
        try {
            if (RequestManager.getInstance(AccountManager.selectedAccount).isUserLogin()) {
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
