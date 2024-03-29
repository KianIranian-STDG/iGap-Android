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
import net.iGap.proto.ProtoChannelAddMember;
import net.iGap.proto.ProtoError;

public class ChannelAddMemberResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public ChannelAddMemberResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();
        ProtoChannelAddMember.ChannelAddMemberResponse.Builder builder = (ProtoChannelAddMember.ChannelAddMemberResponse.Builder) message;
        HelperMember.addMember(builder.getRoomId(), builder.getUserId(), builder.getRole().toString());
        G.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                EventManager.getInstance(AccountManager.selectedAccount).postEvent(EventManager.ON_SUBSCRIBER_OR_MEMBER_COUNT_CHANGE, ((ProtoChannelAddMember.ChannelAddMemberResponse.Builder) message).getRoomId());
            }
        });
        if (G.onChannelAddMember != null) {
            G.onChannelAddMember.onChannelAddMember(builder.getRoomId(), builder.getUserId(), builder.getRole());
        }
    }

    @Override
    public void timeOut() {
        super.timeOut();

        if (G.onChannelAddMember != null) {
            G.onChannelAddMember.onTimeOut();
        }
    }

    @Override
    public void error() {
        super.error();
        ProtoError.ErrorResponse.Builder errorResponse = (ProtoError.ErrorResponse.Builder) message;
        final int majorCode = errorResponse.getMajorCode();
        final int minorCode = errorResponse.getMinorCode();

        if (G.onChannelAddMember != null) {
            G.onChannelAddMember.onError(majorCode, minorCode);
        }
    }
}


