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

import net.iGap.helper.HelperTracker;
import net.iGap.observers.interfaces.OnUserVerification;
import net.iGap.proto.ProtoError;
import net.iGap.proto.ProtoUserVerify;

public class UserVerifyResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public Object identity;

    public UserVerifyResponse(int actionId, Object protoClass, Object identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();
        ProtoUserVerify.UserVerifyResponse.Builder userVerifyResponse = (ProtoUserVerify.UserVerifyResponse.Builder) message;
        if (identity instanceof OnUserVerification) {
            ((OnUserVerification) identity).onUserVerify(userVerifyResponse.getToken(), userVerifyResponse.getNewUser());
        } else {
            throw new ClassCastException("identity must be : " + OnUserVerification.class.getName());
        }
        HelperTracker.sendTracker(HelperTracker.TRACKER_ACTIVATION_CODE);
    }

    @Override
    public void timeOut() {
        super.timeOut();
    }

    @Override
    public void error() {
        super.error();
        ProtoError.ErrorResponse.Builder errorResponse = (ProtoError.ErrorResponse.Builder) message;
        int majorCode = errorResponse.getMajorCode();
        int minorCode = errorResponse.getMinorCode();
        int getWait = errorResponse.getWait();

        if (identity instanceof OnUserVerification) {
            ((OnUserVerification) identity).onUserVerifyError(majorCode, minorCode, getWait);
        } else {
            throw new ClassCastException("identity must be : " + OnUserVerification.class.getName());
        }
    }
}
