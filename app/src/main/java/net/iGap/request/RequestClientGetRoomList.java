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
import net.iGap.module.accountManager.AccountManager;
import net.iGap.network.RequestManager;
import net.iGap.observers.eventbus.EventManager;
import net.iGap.proto.ProtoClientGetRoomList;

public class RequestClientGetRoomList {
    public static boolean isPendingGetRoomList = false;

    public static class IdentityGetRoomList {
        public boolean isFromLogin;
        public String content;
        public int offset;

        IdentityGetRoomList(boolean isFromLogin, int offset, String content) {
            this.isFromLogin = isFromLogin;
            this.offset = offset;
            this.content = content;
        }
    }

    public synchronized boolean clientGetRoomList(int offset, int limit, String identity) {
        if (isPendingGetRoomList) {
            return false;
        }
        ProtoClientGetRoomList.ClientGetRoomList.Builder clientGetRoomList = ProtoClientGetRoomList.ClientGetRoomList.newBuilder();
        clientGetRoomList.setPagination(new RequestPagination().pagination(offset, limit));

        IdentityGetRoomList identityGetRoomList = new IdentityGetRoomList(identity.equals("0"), offset, identity);
        RequestWrapper requestWrapper = new RequestWrapper(601, clientGetRoomList, identityGetRoomList);

        G.runOnUiThread(() -> EventManager.getInstance(AccountManager.selectedAccount).postNotificationName(EventManager.ROOM_LIST_CHANGED, true));

        try {

            if (RequestManager.getInstance(AccountManager.selectedAccount).isUserLogin()) {
                isPendingGetRoomList = true;
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