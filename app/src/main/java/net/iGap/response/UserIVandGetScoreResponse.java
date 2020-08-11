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

import net.iGap.module.accountManager.DbManager;
import net.iGap.observers.interfaces.OnUserIVandGetScore;
import net.iGap.proto.ProtoError;
import net.iGap.proto.ProtoUserIVandGetScore;
import net.iGap.realm.RealmUserInfo;

public class UserIVandGetScoreResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public Object identity;

    public UserIVandGetScoreResponse(int actionId, Object protoClass, Object identity) {
        super(actionId, protoClass, identity);
        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();
        ProtoUserIVandGetScore.UserIVandGetScoreResponse.Builder builder = (ProtoUserIVandGetScore.UserIVandGetScoreResponse.Builder) message;
        DbManager.getInstance().doRealmTransaction(realm -> {
            RealmUserInfo user = realm.where(RealmUserInfo.class).findFirst();
            if (user != null) {
                user.setIvandScore(builder.getScore());
            }
        });
        if (identity instanceof OnUserIVandGetScore) {
            ((OnUserIVandGetScore) identity).getScore(builder);
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
        ((OnUserIVandGetScore) identity).onError(errorResponse.getMajorCode(), errorResponse.getMinorCode());
    }
}


