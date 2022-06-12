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

import net.iGap.module.accountManager.AccountManager;
import net.iGap.observers.interfaces.OnUserProfileSetBioResponse;
import net.iGap.proto.ProtoUserProfileGetBio;
import net.iGap.realm.RealmRegisteredInfo;

public class UserProfileGetBioResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public Object identity;

    public UserProfileGetBioResponse(int actionId, Object protoClass, Object identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();
        ProtoUserProfileGetBio.UserProfileGetBioResponse.Builder builder = (ProtoUserProfileGetBio.UserProfileGetBioResponse.Builder) message;
        RealmRegisteredInfo.updateBio(AccountManager.getInstance().getCurrentUser().getId(), builder.getBio());
        if (identity instanceof OnUserProfileSetBioResponse) {
            ((OnUserProfileSetBioResponse) identity).onUserProfileBioResponse(builder.getBio());
        }
    }

    @Override
    public void timeOut() {
        super.timeOut();
        if (identity instanceof OnUserProfileSetBioResponse) {
            ((OnUserProfileSetBioResponse) identity).timeOut();
        }
    }

    @Override
    public void error() {
        super.error();
    }
}


