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

import net.iGap.proto.ProtoGlobal;
import net.iGap.proto.ProtoUserIVandGetActivities;

public class RequestUserIVandGetActivities {

    public void getActivities(int offset, int limit) {

        ProtoGlobal.Pagination.Builder pagination = ProtoGlobal.Pagination.newBuilder();
        pagination.setLimit(limit)
                .setOffset(offset);

        ProtoUserIVandGetActivities.UserIVandGetActivities.Builder builder = ProtoUserIVandGetActivities.UserIVandGetActivities.newBuilder();
        builder.setPagination(pagination);

        RequestWrapper requestWrapper = new RequestWrapper(153, builder);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
