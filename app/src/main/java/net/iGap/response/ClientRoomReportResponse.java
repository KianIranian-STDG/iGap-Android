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

import net.iGap.G;

public class ClientRoomReportResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public Object identity;

    public ClientRoomReportResponse(int actionId, Object protoClass, Object identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();
        // ProtoClientRoomReport.ClientRoomReportResponse.Builder builder = (ProtoClientRoomReport.ClientRoomReportResponse.Builder) message;
        // no action need to do here
        if (G.onReport != null) {
            G.onReport.success();
        }
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


