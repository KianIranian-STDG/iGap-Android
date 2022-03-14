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
import net.iGap.helper.HelperMember;
import net.iGap.module.accountManager.AccountManager;
import net.iGap.observers.eventbus.EventManager;
import net.iGap.proto.ProtoGroupKickMember;
import net.iGap.realm.RealmRoomAccess;

public class GroupKickMemberResponse extends MessageHandler {

    public GroupKickMemberResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);
    }

    @Override
    public void handler() {
        super.handler();
        ProtoGroupKickMember.GroupKickMemberResponse.Builder builder = (ProtoGroupKickMember.GroupKickMemberResponse.Builder) message;
        HelperMember.kickMember(builder.getRoomId(), builder.getMemberId());

        RealmRoomAccess.getAccess(builder.getMemberId(), builder.getRoomId());
        G.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                EventManager.getInstance(AccountManager.selectedAccount).postEvent(EventManager.ON_SUBSCRIBER_OR_MEMBER_COUNT_CHANGE, ((ProtoGroupKickMember.GroupKickMemberResponse.Builder) message).getRoomId());
            }
        });
    }

    @Override
    public void error() {
        super.error();
    }
}
