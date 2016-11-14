package com.iGap.response;

import com.iGap.G;
import com.iGap.helper.HelperSetAction;
import com.iGap.proto.ProtoFileUploadInit;

public class FileUploadInitResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public FileUploadInitResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();

        ProtoFileUploadInit.FileUploadInitResponse.Builder fileUploadInitResponse =
                (ProtoFileUploadInit.FileUploadInitResponse.Builder) message;

        G.uploaderUtil.OnFileUploadInit(fileUploadInitResponse.getToken(),
                fileUploadInitResponse.getProgress(), fileUploadInitResponse.getOffset(),
                fileUploadInitResponse.getLimit(), this.identity, fileUploadInitResponse.getResponse());
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


