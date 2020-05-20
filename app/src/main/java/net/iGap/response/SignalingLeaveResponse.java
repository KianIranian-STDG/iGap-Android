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
import net.iGap.proto.ProtoSignalingLeave;
import net.iGap.viewmodel.controllers.CallManager;

public class SignalingLeaveResponse extends MessageHandler {
    public SignalingLeaveResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);
    }

    @Override
    public void handler() {
        super.handler();
        final ProtoSignalingLeave.SignalingLeaveResponse.Builder builder = (ProtoSignalingLeave.SignalingLeaveResponse.Builder) message;
        CallManager.getInstance().onLeave(builder);
    }

    @Override
    public void error() {
        super.error();
        ProtoError.ErrorResponse.Builder errorResponse = (ProtoError.ErrorResponse.Builder) message;
        int majorCode = errorResponse.getMajorCode();
        int minorCode = errorResponse.getMinorCode();
        CallManager.getInstance().onError(actionId, majorCode, minorCode);
    }
}


