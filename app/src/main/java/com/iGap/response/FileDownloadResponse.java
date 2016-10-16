package com.iGap.response;

import android.util.Log;

import com.iGap.G;
import com.iGap.module.Utils;
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
        Log.i("BBB", "setAvatar  FileDownloadResponse message : " + message);
        ProtoFileDownload.FileDownloadResponse.Builder builder = (ProtoFileDownload.FileDownloadResponse.Builder) message;

        String[] identityParams = identity.split("\\*");
        String token = identityParams[0];
        ProtoFileDownload.FileDownload.Selector selector = ProtoFileDownload.FileDownload.Selector.valueOf(identityParams[1]);
        long fileSize = Long.parseLong(identityParams[2]);
        String filePath = identityParams[3];
        int previousOffset = Integer.parseInt(identityParams[4]);
        boolean avatarRequested = false;
        long userId = -1;
        if (identityParams.length == 7) {
            avatarRequested = Boolean.parseBoolean(identityParams[5]);
            userId = Long.parseLong(identityParams[6]);
        }
        int nextOffset = previousOffset + builder.getBytes().size();
        int progress = nextOffset * 100 / (int) fileSize;

        Utils.writeBytesToFile(filePath, builder.getBytes().toByteArray(), previousOffset);

        if (!avatarRequested) {
            G.onFileDownloadResponse.onFileDownload(token, nextOffset, selector, progress);
        } else {
            Log.i("NNN", "setAvatar onFileDownloadResponse");
            G.onFileDownloadResponse.onAvatarDownload(token, nextOffset, selector, progress, userId);
        }
    }

    @Override
    public void timeOut() {
        Log.i("SOC", "FileDownloadResponse timeout");
    }

    @Override
    public void error() {
        ProtoError.ErrorResponse.Builder errorResponse = (ProtoError.ErrorResponse.Builder) message;
        int majorCode = errorResponse.getMajorCode();
        int minorCode = errorResponse.getMinorCode();
        Log.i("SOC", "FileDownloadResponse response.majorCode() : " + majorCode);
        Log.i("SOC", "FileDownloadResponse response.minorCode() : " + minorCode);
    }
}


