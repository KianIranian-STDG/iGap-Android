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


import net.iGap.proto.ProtoError;
import net.iGap.proto.ProtoInfoPage;
import net.iGap.request.RequestInfoPage;

public class InfoPageResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public Object identity;

    public InfoPageResponse(int actionId, Object protoClass, Object identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();
        ProtoInfoPage.InfoPageResponse.Builder infoPageResponse = (ProtoInfoPage.InfoPageResponse.Builder) message;
        String body = infoPageResponse.getBody();
        if (identity instanceof RequestInfoPage.OnInfoPage) {
            ((RequestInfoPage.OnInfoPage) identity).onInfo(body);
        }
    }

    @Override
    public void timeOut() {
        super.timeOut();
    }

    @Override
    public void error() {
        super.error();
        if (identity instanceof RequestInfoPage.OnInfoPage) {
            ProtoError.ErrorResponse.Builder errorResponse = (ProtoError.ErrorResponse.Builder) message;
            ((RequestInfoPage.OnInfoPage) identity).onError(errorResponse.getMajorCode(), errorResponse.getMinorCode());
        }
    }
}


