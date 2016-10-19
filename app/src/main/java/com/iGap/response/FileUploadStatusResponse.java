package com.iGap.response;

import android.util.Log;

import com.iGap.G;
import com.iGap.proto.ProtoError;
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
        ProtoFileUploadStatus.FileUploadStatusResponse.Builder builder = (ProtoFileUploadStatus.FileUploadStatusResponse.Builder) message;
        G.onFileUploadStatusResponse.onFileUploadStatus(builder.getStatus(), builder.getProgress(), builder.getRecheckDelayMs(), this.identity, builder.getResponse());
    }

    @Override
    public void timeOut() {
        Log.i("SOC", "FileUploadStatusResponse timeout");
    }

    @Override
    public void error() {
        ProtoError.ErrorResponse.Builder errorResponse = (ProtoError.ErrorResponse.Builder) message;
        int majorCode = errorResponse.getMajorCode();
        int minorCode = errorResponse.getMinorCode();

        Log.i("SOC", "FileUploadStatusResponse response.majorCode() : " + majorCode);
        Log.i("SOC", "FileUploadStatusResponse response.minorCode() : " + minorCode);
    }
}


