/*
 * This is the source code of iGap for Android
 * It is licensed under GNU AGPL v3.0
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright © 2017 , iGap - www.iGap.net
 * iGap Messenger | Free, Fast and Secure instant messaging application
 * The idea of the Kianiranian Company - www.kianiranian.com
 * All rights reserved.
 */

package net.iGap.response;

import net.iGap.G;
import net.iGap.proto.ProtoMplTransactionList;

public class MplTransactionListResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public MplTransactionListResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();

        ProtoMplTransactionList.MplTransactionListResponse.Builder builder = (ProtoMplTransactionList.MplTransactionListResponse.Builder) message;

        if (G.onMplTransaction != null)
            G.onMplTransaction.onMplTransAction(builder.getTransactionList());

    }

    @Override
    public void timeOut() {
        super.timeOut();

        if (G.onMplTransaction != null)
            G.onMplTransaction.onError();

    }

    @Override
    public void error() {
        super.error();

        if (G.onMplTransaction != null)
            G.onMplTransaction.onError();
    }
}