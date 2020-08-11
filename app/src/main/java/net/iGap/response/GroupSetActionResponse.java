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
import net.iGap.helper.HelperGetAction;
import net.iGap.module.accountManager.AccountManager;
import net.iGap.module.accountManager.DbManager;
import net.iGap.proto.ProtoGroupSetAction;

public class GroupSetActionResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public GroupSetActionResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();
        final ProtoGroupSetAction.GroupSetActionResponse.Builder builder = (ProtoGroupSetAction.GroupSetActionResponse.Builder) message;
        DbManager.getInstance().doRealmTransaction(realm -> {
            if (AccountManager.getInstance().getCurrentUser().getId() != builder.getUserId()) {
                if (AccountManager.getInstance().getCurrentUser().getId() != builder.getUserId()) {
                    HelperGetAction.fillOrClearAction(builder.getRoomId(), builder.getUserId(), builder.getAction());
                }
            }
        });

        if (G.onSetAction != null) {
            G.onSetAction.onSetAction(builder.getRoomId(), builder.getUserId(), builder.getAction());
        }

        if (G.onSetActionInRoom != null) {
            G.onSetActionInRoom.onSetAction(builder.getRoomId(), builder.getUserId(), builder.getAction());
        }
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


