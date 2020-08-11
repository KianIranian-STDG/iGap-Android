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

import net.iGap.observers.interfaces.OnUserProfileUpdateUsername;
import net.iGap.proto.ProtoError;
import net.iGap.proto.ProtoUserProfileUpdateUsername;
import net.iGap.realm.RealmUserInfo;

public class UserProfileUpdateUsernameResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public Object identity;

    public UserProfileUpdateUsernameResponse(int actionId, Object protoClass, Object identity) {
        super(actionId, protoClass, identity);
        this.message = protoClass;
        this.identity = identity;
        this.actionId = actionId;
    }

    @Override
    public void handler() {
        super.handler();
        ProtoUserProfileUpdateUsername.UserProfileUpdateUsernameResponse.Builder builder = (ProtoUserProfileUpdateUsername.UserProfileUpdateUsernameResponse.Builder) message;
        RealmUserInfo.updateUsername(builder.getUsername());
        if (identity instanceof OnUserProfileUpdateUsername) {
            ((OnUserProfileUpdateUsername) identity).onUserProfileUpdateUsername(builder.getUsername());
        } else {
            throw new ClassCastException("identity must be : " + OnUserProfileUpdateUsername.class.getName());
        }
    }

    @Override
    public void timeOut() {
        super.timeOut();
        if (identity instanceof OnUserProfileUpdateUsername) {
            ((OnUserProfileUpdateUsername) identity).timeOut();
        } else {
            throw new ClassCastException("identity must be : " + OnUserProfileUpdateUsername.class.getName());
        }
    }

    @Override
    public void error() {
        super.error();
        ProtoError.ErrorResponse.Builder errorResponse = (ProtoError.ErrorResponse.Builder) message;
        int majorCode = errorResponse.getMajorCode();
        int minorCode = errorResponse.getMinorCode();
        int getWait = errorResponse.getWait();

        if (identity instanceof OnUserProfileUpdateUsername) {
            ((OnUserProfileUpdateUsername) identity).Error(majorCode, minorCode, getWait);
        } else {
            throw new ClassCastException("identity must be : " + OnUserProfileUpdateUsername.class.getName());
        }
    }
}


