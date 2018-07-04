/*
 * This is the source code of iGap for Android
 * It is licensed under GNU AGPL v3.0
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright © 2017 , iGap - www.iGap.net
 * iGap Messenger | Free, Fast and Secure instant messaging application
 * The idea of the RooyeKhat Media Company - www.RooyeKhat.co
 * All rights reserved.
 */

package net.iGap.response;

import net.iGap.proto.ProtoBillInquiryMci;

public class BillInquiryMciResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public BillInquiryMciResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();
        ProtoBillInquiryMci.BillInquiryMciResponse.Builder builder = (ProtoBillInquiryMci.BillInquiryMciResponse.Builder) message;

//        uint32 status = 2;
//        string message = 3;
//
//        message BillInfo {
//            uint32 status = 1;
//            uint64 bill_id = 2;
//            uint64 pay_id = 3;
//            uint64 amount = 4;
//            string message = 5;
//        }
//
//        BillInfo mid_term = 4;
//        BillInfo last_term = 5;


    }

    @Override
    public void timeOut() {
        super.timeOut();
    }

    @Override
    public void error() {
        super.error();
    }
}