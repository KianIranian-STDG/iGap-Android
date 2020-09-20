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

import net.iGap.module.accountManager.AccountManager;
import net.iGap.network.RequestManager;
import net.iGap.proto.ProtoUserProfileGetRepresentative;


public class RequestUserProfileGetRepresentative {

    public static int numberOfPendingRequest = 0;

    public interface OnRepresentReady {
        void onRepresent(String phoneNumber);

        void onFailed();
    }

    public void userProfileGetRepresentative(OnRepresentReady onRepresentReady) {

        ProtoUserProfileGetRepresentative.UserProfileGetRepresentative.Builder builder = ProtoUserProfileGetRepresentative.UserProfileGetRepresentative.newBuilder();

        RequestWrapper requestWrapper = new RequestWrapper(151, builder, onRepresentReady);
        if (RequestManager.getInstance(AccountManager.selectedAccount).isUserLogin()) {
            numberOfPendingRequest++;
        }
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
