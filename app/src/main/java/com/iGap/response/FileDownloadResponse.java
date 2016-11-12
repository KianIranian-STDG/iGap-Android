package com.iGap.response;

import android.util.Log;

import com.iGap.G;
import com.iGap.module.AndroidUtils;
import com.iGap.proto.ProtoError;
import com.iGap.proto.ProtoFileDownload;
import com.iGap.realm.enums.RoomType;

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
        super.handler();
        Log.i("BBB", "setAvatar  FileDownloadResponse message : " + message);
        ProtoFileDownload.FileDownloadResponse.Builder builder =
                (ProtoFileDownload.FileDownloadResponse.Builder) message;
        String[] identityParams = identity.split("\\*");
        String token = identityParams[0];
        ProtoFileDownload.FileDownload.Selector selector =
                ProtoFileDownload.FileDownload.Selector.valueOf(identityParams[1]);
        long fileSize = Long.parseLong(identityParams[2]);
        String filePath = G.DIR_TEMP + "/" + identityParams[3];
        Log.i("GGG", "identityParams[3] : " + identityParams[3]);
        Log.i("GGG", "G.DIR_TEMP : " + G.DIR_TEMP);
        int previousOffset = Integer.parseInt(identityParams[4]);
        boolean avatarRequested = false;
        long userId = -1;
        RoomType roomType = RoomType.CHAT;
        if (identityParams.length == 8) {
            avatarRequested = Boolean.parseBoolean(identityParams[5]);
            userId = Long.parseLong(identityParams[6]);
            roomType = RoomType.GROUP;
        }
        int nextOffset = previousOffset + builder.getBytes().size();
        int progress = nextOffset * 100 / (int) fileSize;
        AndroidUtils.writeBytesToFile(filePath, builder.getBytes().toByteArray());
        if (!avatarRequested) {
            G.onFileDownloadResponse.onFileDownload(token, nextOffset, selector, progress);
        } else {
            Log.i("NNN", "setAvatar onFileDownloadResponse");
            G.onFileDownloadResponse.onAvatarDownload(token, nextOffset, selector, progress, userId,
                    roomType);
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

        G.onFileDownloadResponse.onError(majorCode, minorCode);


    }
}


