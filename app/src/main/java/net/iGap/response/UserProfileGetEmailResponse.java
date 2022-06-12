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

import net.iGap.observers.interfaces.OnUserProfileSetEmailResponse;
import net.iGap.observers.interfaces.OnUserProfileSetGenderResponse;
import net.iGap.proto.ProtoUserProfileGetEmail;
import net.iGap.realm.RealmUserInfo;

public class UserProfileGetEmailResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public Object identity;

    public UserProfileGetEmailResponse(int actionId, Object protoClass, Object identity) {
        super(actionId, protoClass, identity);

        this.actionId = actionId;
        this.message = protoClass;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();
        ProtoUserProfileGetEmail.UserProfileGetEmailResponse.Builder builder = (ProtoUserProfileGetEmail.UserProfileGetEmailResponse.Builder) message;
        RealmUserInfo.updateEmail(builder.getEmail());
        if (identity instanceof OnUserProfileSetEmailResponse) {
            ((OnUserProfileSetEmailResponse) identity).onUserProfileEmailResponse(builder.getEmail(),builder.getResponse());
        }
    }

    @Override
    public void timeOut() {
        super.timeOut();
        if (identity instanceof OnUserProfileSetEmailResponse) {
            ((OnUserProfileSetEmailResponse) identity).onTimeOut();
        }
    }

    @Override
    public void error() {
        super.error();
    }
}


