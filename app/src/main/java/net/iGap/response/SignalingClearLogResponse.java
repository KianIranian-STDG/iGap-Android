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
import net.iGap.proto.ProtoSignalingClearLog;
import net.iGap.realm.RealmCallLog;

public class SignalingClearLogResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public Object identity;

    public SignalingClearLogResponse(int actionId, Object protoClass, Object identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();
        ProtoSignalingClearLog.SignalingClearLogResponse.Builder builder = (ProtoSignalingClearLog.SignalingClearLogResponse.Builder) message;

        ProtoSignalingClearLog.SignalingClearLog.Builder builderRequest = (ProtoSignalingClearLog.SignalingClearLog.Builder) identity;

        if (builderRequest.getClearId() != 0) {
            RealmCallLog.clearCallLog(builder.getClearId());
        } else {
            DbManager.getInstance().doRealmTransaction(realm -> {
                for (int i = 0; i < builder.getLogIdCount(); i++) {
                    try {
                        realm.where(RealmCallLog.class).equalTo("logId", builderRequest.getLogIdList().get(i)).findFirst().deleteFromRealm();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }


        /*if (G.onCallLogClear != null) {
            G.onCallLogClear.onCallLogClear();
        }*/
    }

    @Override
    public void timeOut() {
        super.timeOut();
    }

    @Override
    public void error() {
        super.error();
    }
}


