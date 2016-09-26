package com.iGap.response;

import android.util.Log;

import com.iGap.proto.ProtoError;
import com.iGap.proto.ProtoFileDownload;

public class FileDownloadResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;

    public FileDownloadResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }


    @Override
    public void handler() {

        ProtoFileDownload.FileDownloadResponse.Builder builder = (ProtoFileDownload.FileDownloadResponse.Builder) message;
        builder.getBytes();

    }

    @Override
    public void timeOut() {
        Log.i("SOC", "ClientGetRoomResponse timeout");
    }

    @Override
    public void error() {
        ProtoError.ErrorResponse.Builder errorResponse = (ProtoError.ErrorResponse.Builder) message;
        int majorCode = errorResponse.getMajorCode();
        int minorCode = errorResponse.getMinorCode();
        Log.i("SOC", "ClientGetRoomResponse response.majorCode() : " + majorCode);
        Log.i("SOC", "ClientGetRoomResponse response.minorCode() : " + minorCode);
    }
}


