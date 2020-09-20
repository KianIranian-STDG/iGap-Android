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
import net.iGap.proto.ProtoUserUpdateStatus;

public class RequestUserUpdateStatus {

    public interface onUserStatus {
        void onUpdateUserStatus();

        void onError(int major, int minor);
    }

    public boolean userUpdateStatus(ProtoUserUpdateStatus.UserUpdateStatus.Status status) {
        ProtoUserUpdateStatus.UserUpdateStatus.Builder builder = ProtoUserUpdateStatus.UserUpdateStatus.newBuilder();
        builder.setStatus(status);

        RequestWrapper requestWrapper = new RequestWrapper(124, builder);
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

    public boolean userUpdateStatus(ProtoUserUpdateStatus.UserUpdateStatus.Status status, onUserStatus onUserStatus) {
        ProtoUserUpdateStatus.UserUpdateStatus.Builder builder = ProtoUserUpdateStatus.UserUpdateStatus.newBuilder();
        builder.setStatus(status);

        RequestWrapper requestWrapper = new RequestWrapper(124, builder, onUserStatus);
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

