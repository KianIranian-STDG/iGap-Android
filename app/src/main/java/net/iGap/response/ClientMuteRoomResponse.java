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
import net.iGap.proto.ProtoClientMuteRoom;
import net.iGap.realm.RealmRoom;
import net.iGap.realm.RealmRoomFields;

public class ClientMuteRoomResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public ClientMuteRoomResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();
        ProtoClientMuteRoom.ClientMuteRoomResponse.Builder builder = (ProtoClientMuteRoom.ClientMuteRoomResponse.Builder) message;
        DbManager.getInstance().doRealmTransaction(realm -> {
            RealmRoom room = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, builder.getRoomId()).findFirst();
            if (room != null) {
                room.setMute(builder.getRoomMute());
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


