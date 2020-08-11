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

import net.iGap.G;
import net.iGap.module.accountManager.DbManager;
import net.iGap.proto.ProtoGroupAvatarAdd;
import net.iGap.realm.RealmAvatar;

public class GroupAvatarAddResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public GroupAvatarAddResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);
        this.message = protoClass;
        this.identity = identity;
        this.actionId = actionId;
    }

    @Override
    public void handler() {
        super.handler();

        final ProtoGroupAvatarAdd.GroupAvatarAddResponse.Builder groupAvatarAddResponse = (ProtoGroupAvatarAdd.GroupAvatarAddResponse.Builder) message;
        DbManager.getInstance().doRealmTransaction(realm -> {
            RealmAvatar.putOrUpdate(realm, groupAvatarAddResponse.getRoomId(), groupAvatarAddResponse.getAvatar());
        });

        G.handler.post(new Runnable() {
            @Override
            public void run() {
                if (G.onGroupAvatarResponse != null) {
                    G.refreshRealmUi();
                    G.onGroupAvatarResponse.onAvatarAdd(groupAvatarAddResponse.getRoomId(), groupAvatarAddResponse.getAvatar());
                }
            }
        });
    }

    @Override
    public void timeOut() {
        super.timeOut();
    }

    @Override
    public void error() {
        super.error();
        if (G.onGroupAvatarResponse != null) {
            G.onGroupAvatarResponse.onAvatarAddError();
        }
    }
}


