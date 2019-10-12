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

import net.iGap.DbManager;
import net.iGap.G;
import net.iGap.proto.ProtoClientSearchUsername;
import net.iGap.realm.RealmRegisteredInfo;
import net.iGap.realm.RealmRoom;
import net.iGap.realm.RealmRoomFields;

import io.realm.Realm;

public class ClientSearchUsernameResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public ClientSearchUsernameResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();
        ProtoClientSearchUsername.ClientSearchUsernameResponse.Builder builder = (ProtoClientSearchUsername.ClientSearchUsernameResponse.Builder) message;
        DbManager.getInstance().doRealmTask(realm -> {
            for (final ProtoClientSearchUsername.ClientSearchUsernameResponse.Result item : builder.getResultList()) {

                if (item.getType() == ProtoClientSearchUsername.ClientSearchUsernameResponse.Result.Type.USER) {
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            RealmRegisteredInfo.putOrUpdate(realm, item.getUser());
                        }
                    });
                } else if (item.getType() == ProtoClientSearchUsername.ClientSearchUsernameResponse.Result.Type.ROOM) {
                    if (realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, item.getRoom().getId()).findFirst() == null) {
                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                RealmRoom realmRoom = RealmRoom.putOrUpdate(item.getRoom(), realm);
                                realmRoom.setDeleted(true);
                            }
                        });
                    }
                }
            }
        });
        if (G.onClientSearchUserName != null) {
            G.onClientSearchUserName.OnGetList(builder);
        }
    }

    @Override
    public void timeOut() {
        super.timeOut();
    }

    @Override
    public void error() {
        super.error();

        if (G.onClientSearchUserName != null) {
            G.onClientSearchUserName.OnErrore();
        }
    }
}


