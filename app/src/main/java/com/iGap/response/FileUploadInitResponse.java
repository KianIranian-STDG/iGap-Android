package com.iGap.response;

import android.util.Log;

import com.iGap.G;
import com.iGap.proto.ProtoError;
import com.iGap.proto.ProtoFileUploadInit;
import com.iGap.proto.ProtoResponse;

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

        ProtoFileUploadInit.FileUploadInitResponse.Builder fileUploadInitResponse =
                (ProtoFileUploadInit.FileUploadInitResponse.Builder) message;

        ProtoResponse.Response.Builder response =
                ProtoResponse.Response.newBuilder().mergeFrom(fileUploadInitResponse.getResponse());
        Log.i("SOC", "FileUploadInitResponse response.getId() : " + response.getId());
        Log.i("SOC", "FileUploadInitResponse response.getTimestamp() : " + response.getTimestamp());
        G.uploaderUtil.OnFileUploadInit(fileUploadInitResponse.getToken(),
                fileUploadInitResponse.getProgress(), fileUploadInitResponse.getOffset(),
                fileUploadInitResponse.getLimit(), this.identity, fileUploadInitResponse.getResponse());
    }

    @Override
    public void timeOut() {
        Log.i("SOC", "FileUploadInitResponse timeout");
    }

    @Override
    public void error() {
        ProtoError.ErrorResponse.Builder errorResponse = (ProtoError.ErrorResponse.Builder) message;
        int majorCode = errorResponse.getMajorCode();
        int minorCode = errorResponse.getMinorCode();

        Log.i("SOC", "FileUploadInitResponse response.majorCode() : " + majorCode);
        Log.i("SOC", "FileUploadInitResponse response.minorCode() : " + minorCode);
    }
}


