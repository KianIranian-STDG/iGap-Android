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
import net.iGap.observers.interfaces.OnUserDelete;
import net.iGap.proto.ProtoError;

import java.io.File;

import static net.iGap.module.FileUtils.deleteRecursive;

public class UserDeleteResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public Object identity;

    public UserDeleteResponse(int actionId, Object protoClass, Object identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();
        G.logoutAccount.postValue(true);
        deleteRecursive(new File(G.DIR_APP));
        ((OnUserDelete) identity).onUserDeleteResponse();
    }

    @Override
    public void error() {
        super.error();
        ProtoError.ErrorResponse.Builder errorResponse = (ProtoError.ErrorResponse.Builder) message;
        ((OnUserDelete) identity).Error(errorResponse.getMajorCode(), errorResponse.getMinorCode(), errorResponse.getWait());
    }
}


