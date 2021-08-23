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
import net.iGap.module.accountManager.DbManager;
import net.iGap.proto.ProtoClientRegisterDevice;
import net.iGap.proto.ProtoError;
import net.iGap.realm.RealmUserInfo;

import io.realm.Realm;

public class ClientRegisterDeviceResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public Object identity;

    public ClientRegisterDeviceResponse(int actionId, Object protoClass, Object identity) { // here identity is roomId and messageId
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();
        ProtoClientRegisterDevice.ClientRegisterDeviceResponse.Builder builder =
                (ProtoClientRegisterDevice.ClientRegisterDeviceResponse.Builder) message;
        DbManager.getInstance().doRealmTransaction(realm -> realm.where(RealmUserInfo.class)
                .equalTo("userInfo.id", AccountManager.getInstance().getCurrentUser().getId())
                .findFirst().setPushNotificationToken(""));
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
    }
}


