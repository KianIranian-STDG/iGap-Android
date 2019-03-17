/*
* This is the source code of iGap for Android
* It is licensed under GNU AGPL v3.0
* You should have received a copy of the license in this archive (see LICENSE).
* Copyright © 2017 , iGap - www.iGap.net
* iGap Messenger | Free, Fast and Secure instant messaging application
* The idea of the RooyeKhat Media Company - www.RooyeKhat.co
* All rights reserved.
*/

package net.iGap.request;

import net.iGap.proto.ProtoClientGetRoomList;

public class RequestClientGetRoomList {

    public static boolean isLoadingRoomListOffsetZero = false;
    private static final Integer mutex = 1;

    public static class IdentityGetRoomList {
        public boolean isFromLogin;
        public boolean isOffsetZero;
        public String content;

        IdentityGetRoomList(boolean isFromLogin, boolean isOffsetZero, String content) {
            this.isFromLogin = isFromLogin;
            this.isOffsetZero = isOffsetZero;
            this.content = content;
        }
    }

    public void clientGetRoomList(int offset, int limit, String identity) {
        if (offset == 0) {
            synchronized(mutex) {
                if (isLoadingRoomListOffsetZero) {
                    return;
                } else {
                    isLoadingRoomListOffsetZero = true;
                }
            }
        }
        ProtoClientGetRoomList.ClientGetRoomList.Builder clientGetRoomList = ProtoClientGetRoomList.ClientGetRoomList.newBuilder();
        clientGetRoomList.setPagination(new RequestPagination().pagination(offset, limit));

        IdentityGetRoomList identityGetRoomList = new IdentityGetRoomList(identity.equals("0"), offset == 0, identity);
        RequestWrapper requestWrapper = new RequestWrapper(601, clientGetRoomList, identityGetRoomList);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}