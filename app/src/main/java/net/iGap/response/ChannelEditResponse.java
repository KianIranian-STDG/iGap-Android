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

import net.iGap.observers.interfaces.OnChannelEdit;
import net.iGap.proto.ProtoChannelEdit;
import net.iGap.proto.ProtoError;
import net.iGap.realm.RealmRoom;

public class ChannelEditResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public Object identity;

    public ChannelEditResponse(int actionId, Object protoClass, Object identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();
        ProtoChannelEdit.ChannelEditResponse.Builder builder = (ProtoChannelEdit.ChannelEditResponse.Builder) message;
        RealmRoom.editRoom(builder.getRoomId(), builder.getName(), builder.getDescription());

        if (identity instanceof OnChannelEdit) {
            ((OnChannelEdit) identity).onChannelEdit(builder.getRoomId(), builder.getName(), builder.getDescription());
        } else {
            throw new ClassCastException("identity must be : " + OnChannelEdit.class.getName());
        }
    }

    @Override
    public void timeOut() {
        super.timeOut();
        if (identity instanceof OnChannelEdit) {
            ((OnChannelEdit) identity).onTimeOut();
        } else {
            throw new ClassCastException("identity must be : " + OnChannelEdit.class.getName());
        }
    }

    @Override
    public void error() {
        super.error();
        ProtoError.ErrorResponse.Builder errorResponse = (ProtoError.ErrorResponse.Builder) message;
        int majorCode = errorResponse.getMajorCode();
        int minorCode = errorResponse.getMinorCode();
        if (identity instanceof OnChannelEdit) {
            ((OnChannelEdit) identity).onError(majorCode, minorCode);
        } else {
            throw new ClassCastException("identity must be : " + OnChannelEdit.class.getName());
        }
    }
}


