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
import net.iGap.proto.ProtoUserIVandSetActivity;
import net.iGap.request.RequestUserIVandSetActivity;

public class UserIVandSetActivityResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public Object identity;

    public UserIVandSetActivityResponse(int actionId, Object protoClass, Object identity) {
        super(actionId, protoClass, identity);
        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();
        ProtoUserIVandSetActivity.UserIVandSetActivityResponse.Builder builder = (ProtoUserIVandSetActivity.UserIVandSetActivityResponse.Builder) message;
        ((RequestUserIVandSetActivity.OnSetActivities) identity).onSetActivitiesReady(builder.getMessage(), builder.getState());
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

        ((RequestUserIVandSetActivity.OnSetActivities) identity).onError(majorCode, minorCode);

    }
}


