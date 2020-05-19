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
import net.iGap.proto.ProtoSignalingSessionHold;
import net.iGap.viewmodel.controllers.CallManager;

public class SignalingSessionHoldResponse extends MessageHandler {

    public SignalingSessionHoldResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);
    }

    @Override
    public void handler() {
        super.handler();
        ProtoSignalingSessionHold.SignalingSessionHoldResponse.Builder builder = (ProtoSignalingSessionHold.SignalingSessionHoldResponse.Builder) message;
        CallManager.getInstance().onHold(builder);
    }

    @Override
    public void error() {
        super.error();
        ProtoError.ErrorResponse.Builder errorResponse = (ProtoError.ErrorResponse.Builder) message;
        int majorCode = errorResponse.getMajorCode();
        int minorCode = errorResponse.getMinorCode();
        CallManager.getInstance().onError(majorCode, minorCode);
    }
}


