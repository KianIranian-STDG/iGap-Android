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

import net.iGap.fragments.inquiryBill.BillInquiryResponse;
import net.iGap.observers.interfaces.GeneralResponseCallBack;
import net.iGap.proto.ProtoBillInquiryMci;
import net.iGap.proto.ProtoError;

public class BillInquiryMciResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public Object identity;

    public BillInquiryMciResponse(int actionId, Object protoClass, Object identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();

        ProtoBillInquiryMci.BillInquiryMciResponse.Builder data = (ProtoBillInquiryMci.BillInquiryMciResponse.Builder) message;
        BillInquiryResponse response = new BillInquiryResponse(data.getMidTerm(), data.getLastTerm());
        if (identity instanceof GeneralResponseCallBack) {
            ((GeneralResponseCallBack) identity).onSuccess(response);
        }

    }

    @Override
    public void timeOut() {
        super.timeOut();
    }

    @Override
    public void error() {
        super.error();
        ProtoError.ErrorResponse.Builder errorResponse = (ProtoError.ErrorResponse.Builder) message;
        if (identity instanceof GeneralResponseCallBack) {
            ((GeneralResponseCallBack) identity).onError(errorResponse.getMajorCode(), errorResponse.getMinorCode());
        }
    }
}