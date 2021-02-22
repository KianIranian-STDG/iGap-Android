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

import net.iGap.controllers.RoomController;
import net.iGap.module.accountManager.AccountManager;
import net.iGap.proto.ProtoError;
import net.iGap.realm.RealmRoom;
import net.iGap.request.RequestClientJoinByUsername;

public class ClientJoinByUsernameResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public Object identity;

    public ClientJoinByUsernameResponse(int actionId, Object protoClass, Object identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();

        if (identity instanceof RequestClientJoinByUsername.OnClientJoinByUsername) {
            ((RequestClientJoinByUsername.OnClientJoinByUsername) identity).onClientJoinByUsernameResponse();
        } else if (identity instanceof Long) {
            long roomId = (long) identity;
            RealmRoom.joinRoom(roomId);
            RoomController.getInstance(AccountManager.selectedAccount).clientPinRoom(roomId, true);
        }
    }

    @Override
    public void timeOut() {
        super.timeOut();
    }

    @Override
    public void error() {
        super.error();
        ProtoError.ErrorResponse.Builder errorResponse = (ProtoError.ErrorResponse.Builder) message;
        int majorCode = errorResponse.getMajorCode();
        int minorCode = errorResponse.getMinorCode();
        if (identity instanceof RequestClientJoinByUsername.OnClientJoinByUsername) {
            ((RequestClientJoinByUsername.OnClientJoinByUsername) identity).onError(majorCode, minorCode);
        } else if (identity instanceof Long) {
        }
    }
}


