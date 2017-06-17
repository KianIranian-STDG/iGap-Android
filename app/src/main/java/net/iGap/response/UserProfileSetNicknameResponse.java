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

import io.realm.Realm;
import net.iGap.G;
import net.iGap.helper.HelperImageBackColor;
import net.iGap.proto.ProtoError;
import net.iGap.proto.ProtoUserProfileNickname;
import net.iGap.realm.RealmRegisteredInfo;
import net.iGap.realm.RealmUserInfo;

public class UserProfileSetNicknameResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public UserProfileSetNicknameResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();

        final ProtoUserProfileNickname.UserProfileSetNicknameResponse.Builder userProfileNickNameResponse = (ProtoUserProfileNickname.UserProfileSetNicknameResponse.Builder) message;

        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmRegisteredInfo realmRegisteredInfo = realm.where(RealmUserInfo.class).findFirst().getUserInfo();
                if (realmRegisteredInfo != null) {
                    realmRegisteredInfo.setDisplayName(userProfileNickNameResponse.getNickname());
                    realmRegisteredInfo.setInitials(HelperImageBackColor.getFirstAlphabetName(userProfileNickNameResponse.getNickname()));
                    G.displayName = userProfileNickNameResponse.getNickname();
                }
            }
        });

        realm.close();

        if (G.onUserProfileSetNickNameResponse != null) {
            G.onUserProfileSetNickNameResponse.onUserProfileNickNameResponse(userProfileNickNameResponse.getNickname(), userProfileNickNameResponse.getResponse());
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
        final int majorCode = errorResponse.getMajorCode();
        final int minorCode = errorResponse.getMinorCode();

        if (G.onUserProfileSetNickNameResponse != null) {
            G.onUserProfileSetNickNameResponse.onUserProfileNickNameError(majorCode, minorCode);
        }
    }
}


