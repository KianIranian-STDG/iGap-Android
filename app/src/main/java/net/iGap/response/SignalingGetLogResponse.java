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

import java.util.List;
import net.iGap.proto.ProtoSignalingGetLog;

public class SignalingGetLogResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public SignalingGetLogResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();

        ProtoSignalingGetLog.SignalingGetLogResponse.Builder builder = (ProtoSignalingGetLog.SignalingGetLogResponse.Builder) message;

        List<ProtoSignalingGetLog.SignalingGetLogResponse.SignalingLog> list = builder.getSignalingLogList();

        for (ProtoSignalingGetLog.SignalingGetLogResponse.SignalingLog item : list) {

            item.getId();
            item.getType();
            item.getStatus();
            item.getPeer();
            item.getOfferTime();
            item.getDuration();

            // TODO: 5/8/2017  nejati  make callLog in dp and add this item on it
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


