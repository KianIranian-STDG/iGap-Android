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
import net.iGap.module.enums.GroupChatRole;
import net.iGap.observers.interfaces.OnResponse;
import net.iGap.proto.ProtoGlobal;
import net.iGap.proto.ProtoGroupChangeMemberRights;
import net.iGap.realm.RealmRoom;
import net.iGap.realm.RealmRoomAccess;
import net.iGap.realm.RealmRoomFields;

public class GroupChangeMemberRightsResponse extends MessageHandler {

    public GroupChangeMemberRightsResponse(int actionId, Object protoClass, Object identity) {
        super(actionId, protoClass, identity);
    }

    @Override
    public void handler() {
        super.handler();

        ProtoGroupChangeMemberRights.GroupChangeMemberRightsResponse.Builder builder = (ProtoGroupChangeMemberRights.GroupChangeMemberRightsResponse.Builder) message;

        DbManager.getInstance().doRealmTask(realm -> {
            RealmRoom realmRoom = realm.where(RealmRoom.class).equalTo(RealmRoomFields.ID, builder.getRoomId()).findFirst();

            if (realmRoom != null && realmRoom.getType() == ProtoGlobal.Room.Type.GROUP) {
                if (realmRoom.getGroupRoom().getRole() == GroupChatRole.OWNER || realmRoom.getGroupRoom().getRole() == GroupChatRole.ADMIN) {
                    if (builder.getUserId() == 0 || builder.getUserId() != AccountManager.getInstance().getCurrentUser().getId())
                        realm.executeTransaction(asyncRealm -> RealmRoomAccess.groupMemberPutOrUpdate(builder.getPermission(), builder.getUserId(), builder.getRoomId(), asyncRealm));
                } else if (builder.getUserId() == 0) {
                    realm.executeTransaction(asyncRealm -> RealmRoomAccess.groupMemberPutOrUpdate(builder.getPermission(), AccountManager.getInstance().getCurrentUser().getId(), builder.getRoomId(), asyncRealm));
                } else {
                    realm.executeTransaction(asyncRealm -> RealmRoomAccess.groupMemberPutOrUpdate(builder.getPermission(), builder.getUserId(), builder.getRoomId(), asyncRealm));
                }
            }
        });

        if (identity instanceof OnResponse) {
            ((OnResponse) identity).onReceived(message, null);
        }
    }

    @Override
    public void error() {
        super.error();
        if (identity instanceof OnResponse) {
            ((OnResponse) identity).onReceived(null, message);
        }
    }
}


