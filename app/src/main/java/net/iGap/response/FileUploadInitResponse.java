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

import net.iGap.proto.ProtoFileUploadInit;
import net.iGap.request.RequestFileUploadInit;

public class FileUploadInitResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public Object identity;

    public FileUploadInitResponse(int actionId, Object protoClass, Object identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();

        ProtoFileUploadInit.FileUploadInitResponse.Builder fp = (ProtoFileUploadInit.FileUploadInitResponse.Builder) message;
        ((RequestFileUploadInit.OnFileUploadInit) identity).onFileUploadInit(fp.getToken(), fp.getProgress(), fp.getOffset(), fp.getLimit());
    }

    @Override
    public void timeOut() {
        super.timeOut();
    }

    @Override
    public void error() {
        super.error();
        ((RequestFileUploadInit.OnFileUploadInit) identity).onFileUploadInitError(majorCode, minorCode);
    }

}


