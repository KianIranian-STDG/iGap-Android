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

import android.util.Log;

import net.iGap.proto.ProtoClientGetRoomList;

import java.util.HashSet;

public class RequestClientGetRoomList {

    public static HashSet<Integer> pendingRequest = new HashSet<>();
    public static boolean isLoadingRoomListOffsetZero = false;
    private static final Integer mutex = 1;

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

    public boolean clientGetRoomList(int offset, int limit, String identity) {
        Log.d("bagi" , "clientGetRoomList" + offset + "" + limit + identity);
        if (offset == 0) {
            synchronized(mutex) {
                if (isLoadingRoomListOffsetZero) {
                    return false;
                } else {
                    isLoadingRoomListOffsetZero = true;
                }
            }
        }

        synchronized(mutex) {
            if (pendingRequest.contains(offset)) {
                return false;
            } else {
                pendingRequest.add(offset);
            }
        }

        ProtoClientGetRoomList.ClientGetRoomList.Builder clientGetRoomList = ProtoClientGetRoomList.ClientGetRoomList.newBuilder();
        clientGetRoomList.setPagination(new RequestPagination().pagination(offset, limit));

        IdentityGetRoomList identityGetRoomList = new IdentityGetRoomList(identity.equals("0"), offset, identity);
        RequestWrapper requestWrapper = new RequestWrapper(601, clientGetRoomList, identityGetRoomList);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return true;
    }
}