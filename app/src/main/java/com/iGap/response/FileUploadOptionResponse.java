package com.iGap.response;

import com.iGap.G;
import com.iGap.helper.HelperSetAction;
import com.iGap.proto.ProtoFileUploadOption;

public class FileUploadOptionResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public FileUploadOptionResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {

        ProtoFileUploadOption.FileUploadOptionResponse.Builder fileUploadOptionResponse =
                (ProtoFileUploadOption.FileUploadOptionResponse.Builder) message;

        G.uploaderUtil.OnFileUploadOption(fileUploadOptionResponse.getFirstBytesLimit(),
                fileUploadOptionResponse.getLastBytesLimit(),
                fileUploadOptionResponse.getMaxConnection(), this.identity,
                fileUploadOptionResponse.getResponse());
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


