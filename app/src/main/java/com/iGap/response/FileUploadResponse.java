package com.iGap.response;

import com.iGap.G;
import com.iGap.helper.HelperSetAction;
import com.iGap.proto.ProtoFileUpload;

public class FileUploadResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public FileUploadResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();
        ProtoFileUpload.FileUploadResponse.Builder fileUploadResponse = (ProtoFileUpload.FileUploadResponse.Builder) message;
        G.uploaderUtil.onFileUpload(fileUploadResponse.getProgress(), fileUploadResponse.getNextOffset(), fileUploadResponse.getNextLimit(), this.identity, fileUploadResponse.getResponse());
    }

    @Override
    public void timeOut() {
        HelperSetAction.sendCancel(Long.parseLong(this.identity));
        super.timeOut();
    }

    @Override
    public void error() {
        HelperSetAction.sendCancel(Long.parseLong(this.identity));
        super.error();
    }
}


