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
import net.iGap.proto.ProtoChannelAvatarAdd;
import net.iGap.realm.RealmAvatar;

public class ChannelAvatarAddResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public ChannelAvatarAddResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();

        final ProtoChannelAvatarAdd.ChannelAvatarAddResponse.Builder builder = (ProtoChannelAvatarAdd.ChannelAvatarAddResponse.Builder) message;
        DbManager.getInstance().doRealmTransaction(realm -> {
            RealmAvatar.putOrUpdate(realm, builder.getRoomId(), builder.getAvatar());
        });
        G.handler.post(new Runnable() {
            @Override
            public void run() {
                if (G.onChannelAvatarAdd != null) {
                    G.refreshRealmUi();
                    G.onChannelAvatarAdd.onAvatarAdd(builder.getRoomId(), builder.getAvatar());
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
    }
}


