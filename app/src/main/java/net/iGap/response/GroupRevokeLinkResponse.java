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

import net.iGap.observers.interfaces.OnGroupRevokeLink;
import net.iGap.proto.ProtoError;
import net.iGap.proto.ProtoGroupRevokeLink;
import net.iGap.realm.RealmGroupRoom;

public class GroupRevokeLinkResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public Object identity;

    public GroupRevokeLinkResponse(int actionId, Object protoClass, Object identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();
        ProtoGroupRevokeLink.GroupRevokeLinkResponse.Builder builder = (ProtoGroupRevokeLink.GroupRevokeLinkResponse.Builder) message;
        RealmGroupRoom.revokeLink(builder.getRoomId(), builder.getInviteLink(), builder.getInviteToken());
        if (identity instanceof OnGroupRevokeLink) {
            ((OnGroupRevokeLink) identity).onGroupRevokeLink(builder.getRoomId(), "https://" + builder.getInviteLink(), builder.getInviteToken());
        } else {
            throw new ClassCastException("identity must be : " + OnGroupRevokeLink.class.getName());
        }
    }

    @Override
    public void timeOut() {
        super.timeOut();
        if (identity instanceof OnGroupRevokeLink) {
            ((OnGroupRevokeLink) identity).onTimeOut();
        } else {
            throw new ClassCastException("identity must be : " + OnGroupRevokeLink.class.getName());
        }
    }

    @Override
    public void error() {
        super.error();
        ProtoError.ErrorResponse.Builder errorResponse = (ProtoError.ErrorResponse.Builder) message;
        if (identity instanceof OnGroupRevokeLink) {
            ((OnGroupRevokeLink) identity).onError(errorResponse.getMajorCode(), errorResponse.getMinorCode());
        } else {
            throw new ClassCastException("identity must be : " + OnGroupRevokeLink.class.getName());
        }
    }
}


