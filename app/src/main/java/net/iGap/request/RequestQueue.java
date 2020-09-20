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

import net.iGap.module.accountManager.AccountManager;
import net.iGap.network.RequestManager;

public class RequestQueue {

    public static String sendRequest(RequestWrapper... requestWrappers) throws IllegalAccessException {
        return RequestManager.getInstance(AccountManager.selectedAccount).sendRequest(requestWrappers[0]);
    }

    public static boolean cancelRequest(String id) {
        return RequestManager.getInstance(AccountManager.selectedAccount).cancelRequest(id);
    }
}