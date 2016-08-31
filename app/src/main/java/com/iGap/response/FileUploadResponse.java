package com.iGap.response;

import android.util.Log;

import com.iGap.G;
import com.iGap.proto.ProtoError;
import com.iGap.proto.ProtoFileUpload;
import com.iGap.proto.ProtoResponse;

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

        ProtoFileUpload.FileUploadResponse.Builder fileUploadResponse = (ProtoFileUpload.FileUploadResponse.Builder) message;

        ProtoResponse.Response.Builder response = ProtoResponse.Response.newBuilder().mergeFrom(fileUploadResponse.getResponse());
        Log.i("SOC", "FileUploadResponse response.getId() : " + response.getId());
        Log.i("SOC", "FileUploadResponse response.getTimestamp() : " + response.getTimestamp());
        G.uploaderUtil.onFileUpload(fileUploadResponse.getProgress(), fileUploadResponse.getNextOffset(), fileUploadResponse.getNextLimit(), this.identity, fileUploadResponse.getResponse());
    }

    @Override
    public void timeOut() {
        Log.i("SOC", "FileUploadResponse timeout");
    }

    @Override
    public void error() {
        ProtoError.ErrorResponse.Builder errorResponse = (ProtoError.ErrorResponse.Builder) message;
        int majorCode = errorResponse.getMajorCode();
        int minorCode = errorResponse.getMinorCode();

        Log.i("SOC", "FileUploadResponse response.majorCode() : " + majorCode);
        Log.i("SOC", "FileUploadResponse response.minorCode() : " + minorCode);
    }
}


