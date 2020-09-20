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
import net.iGap.proto.ProtoClientSetDiscoveryItemAgreement;

public class RequestClientSetDiscoveryItemAgreement {

    public interface OnSetAgreement {
        void onSet();

        void onError(int major, int minor);
    }

    public boolean setAgreement(OnSetAgreement onSetAgreement, int id) {
        ProtoClientSetDiscoveryItemAgreement.ClientSetDiscoveryItemAgreement.Builder builder = ProtoClientSetDiscoveryItemAgreement.ClientSetDiscoveryItemAgreement.newBuilder();
        builder.setItemId(id);

        RequestWrapper requestWrapper = new RequestWrapper(623, builder, onSetAgreement);
        try {
            if (RequestManager.getInstance(AccountManager.selectedAccount).isUserLogin()) {
                RequestQueue.sendRequest(requestWrapper);
                return true;
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return false;
    }
}
