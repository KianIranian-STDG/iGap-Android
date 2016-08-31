package com.iGap.response;

import android.util.Log;

import com.iGap.G;
import com.iGap.proto.ProtoError;
import com.iGap.proto.ProtoFileUpload;
import com.iGap.proto.ProtoResponse;

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

        ProtoFileUpload.FileUploadOptionResponse.Builder fileUploadOptionResponse = (ProtoFileUpload.FileUploadOptionResponse.Builder) message;

        ProtoResponse.Response.Builder response = ProtoResponse.Response.newBuilder().mergeFrom(fileUploadOptionResponse.getResponse());
        Log.i("SOC", "FileUploadOptionResponse response.getId() : " + response.getId());
        Log.i("SOC", "FileUploadOptionResponse response.getTimestamp() : " + response.getTimestamp());
        G.uploaderUtil.OnFileUploadOption(fileUploadOptionResponse.getFirstBytesLimit(), fileUploadOptionResponse.getLastBytesLimit(), fileUploadOptionResponse.getMaxConnection(), this.identity, fileUploadOptionResponse.getResponse());
    }

    @Override
    public void timeOut() {
        Log.i("SOC", "FileUploadOptionResponse timeout");
    }

    @Override
    public void error() {

        ProtoError.ErrorResponse.Builder errorResponse = (ProtoError.ErrorResponse.Builder) message;
        int majorCode = errorResponse.getMajorCode();
        int minorCode = errorResponse.getMinorCode();
        Log.i("SOC", "FileUploadOptionResponse response.majorCode() : " + majorCode);
        Log.i("SOC", "FileUploadOptionResponse response.minorCode() : " + minorCode);
    }
}


