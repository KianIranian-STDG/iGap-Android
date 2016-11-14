package com.iGap.response;

import com.iGap.G;
import com.iGap.helper.HelperSetAction;
import com.iGap.proto.ProtoFileUploadStatus;

public class FileUploadStatusResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public FileUploadStatusResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();
        ProtoFileUploadStatus.FileUploadStatusResponse.Builder builder = (ProtoFileUploadStatus.FileUploadStatusResponse.Builder) message;
        G.onFileUploadStatusResponse.onFileUploadStatus(builder.getStatus(), builder.getProgress(), builder.getRecheckDelayMs(), this.identity, builder.getResponse());
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


