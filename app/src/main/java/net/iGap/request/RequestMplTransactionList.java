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
import net.iGap.proto.ProtoMplTransactionList;

public class RequestMplTransactionList {

    public void mplTransactionList(ProtoGlobal.MplTransaction.Type type, int offset, int limit) {

        ProtoMplTransactionList.MplTransactionList.Builder builder = ProtoMplTransactionList.MplTransactionList.newBuilder();
        builder.setType(type);

        ProtoGlobal.Pagination.Builder pagination = ProtoGlobal.Pagination.newBuilder();
        pagination.setLimit(limit);
        pagination.setOffset(offset);
        builder.setPagination(pagination);

        RequestWrapper requestWrapper = new RequestWrapper(9109, builder);
        try {
            RequestQueue.sendRequest(requestWrapper);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
