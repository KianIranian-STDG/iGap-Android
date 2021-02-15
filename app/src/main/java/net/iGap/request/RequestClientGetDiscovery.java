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

import net.iGap.BuildConfig;
import net.iGap.fragments.discovery.OnDiscoveryList;
import net.iGap.module.accountManager.AccountManager;
import net.iGap.network.RequestManager;
import net.iGap.proto.ProtoClientGetDiscovery;

public class RequestClientGetDiscovery {

    public boolean getDiscovery(int pageId, OnDiscoveryList discoveryListener) {

        ProtoClientGetDiscovery.ClientGetDiscovery.Builder builder = ProtoClientGetDiscovery.ClientGetDiscovery.newBuilder();
        builder.setProjectId(BuildConfig.APP_ID);
        builder.setPageId(pageId);

        RequestWrapper requestWrapper = new RequestWrapper(620, builder, discoveryListener);
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
