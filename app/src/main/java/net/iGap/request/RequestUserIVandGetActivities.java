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
import net.iGap.proto.ProtoGlobal;
import net.iGap.proto.ProtoUserIVandGetActivities;

import java.util.ArrayList;

public class RequestUserIVandGetActivities {

    public interface OnGetActivities {
        void onGetActivitiesReady(ProtoGlobal.Pagination pagination, ArrayList<ProtoGlobal.IVandActivity> discoveryArrayList);

        void onError(ProtoGlobal.Pagination pagination);
    }

    public class GetActivityStruct {
        public OnGetActivities onGetActivities;
        public ProtoGlobal.Pagination pagination;

        public GetActivityStruct(OnGetActivities onGetActivities, ProtoGlobal.Pagination pagination) {
            this.onGetActivities = onGetActivities;
            this.pagination = pagination;
        }
    }

    public boolean getActivities(int offset, int limit, OnGetActivities onGetActivities) {

        ProtoGlobal.Pagination.Builder pagination = ProtoGlobal.Pagination.newBuilder();
        pagination.setLimit(limit)
                .setOffset(offset);

        ProtoUserIVandGetActivities.UserIVandGetActivities.Builder builder = ProtoUserIVandGetActivities.UserIVandGetActivities.newBuilder();
        builder.setPagination(pagination);
        RequestWrapper requestWrapper = new RequestWrapper(153, builder, new GetActivityStruct(onGetActivities, pagination.build()));
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
