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

import net.iGap.helper.HelperMember;
import net.iGap.module.accountManager.DbManager;
import net.iGap.module.enums.ChannelChatRole;
import net.iGap.observers.interfaces.OnResponse;
import net.iGap.proto.ProtoGroupKickAdmin;
import net.iGap.realm.RealmRoomAccess;

public class GroupKickAdminResponse extends MessageHandler {
    public GroupKickAdminResponse(int actionId, Object protoClass, Object identity) {
        super(actionId, protoClass, identity);
    }

    @Override
    public void handler() {
        super.handler();
        ProtoGroupKickAdmin.GroupKickAdminResponse.Builder builder = (ProtoGroupKickAdmin.GroupKickAdminResponse.Builder) message;
        HelperMember.updateRole(builder.getRoomId(), builder.getMemberId(), ChannelChatRole.MEMBER.toString());

        DbManager.getInstance().doRealmTask(realm -> {
            realm.executeTransaction(asyncRealm -> RealmRoomAccess.groupMemberPutOrUpdate(builder.getPermission(), builder.getMemberId(), builder.getRoomId(), asyncRealm));
        });

        if (identity instanceof OnResponse) {
            ((OnResponse) identity).onReceived(message, null);
        }
    }

    @Override
    public void error() {
        super.error();
        if (identity instanceof OnResponse)
            ((OnResponse) identity).onReceived(null, message);
    }
}
