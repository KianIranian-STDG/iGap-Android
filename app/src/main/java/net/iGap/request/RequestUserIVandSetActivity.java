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
import net.iGap.proto.ProtoUserIVandSetActivity;


public class RequestUserIVandSetActivity {

    public interface OnSetActivities {
        void onSetActivitiesReady(String message, boolean isOk);
        void onError();
    }

    public boolean setActivity(String value, OnSetActivities onSetActivities) {

        ProtoUserIVandSetActivity.UserIVandSetActivity.Builder builder = ProtoUserIVandSetActivity.UserIVandSetActivity.newBuilder();
        builder.setPlancode(value);

        RequestWrapper requestWrapper = new RequestWrapper(155, builder, onSetActivities);
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
