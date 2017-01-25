package com.iGap.response;

import com.iGap.G;
import com.iGap.module.AndroidUtils;
import com.iGap.proto.ProtoError;
import com.iGap.proto.ProtoFileDownload;

public class FileDownloadResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public String identity;
    public boolean isFromHelperDownload = false;

    public FileDownloadResponse(int actionId, Object protoClass, String identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override public void handler() {

        super.handler();
        ProtoFileDownload.FileDownloadResponse.Builder builder = (ProtoFileDownload.FileDownloadResponse.Builder) message;
        String[] identityParams = identity.split("\\*");
        String token = identityParams[0];
        ProtoFileDownload.FileDownload.Selector selector = ProtoFileDownload.FileDownload.Selector.valueOf(identityParams[1]);
        long fileSize = Long.parseLong(identityParams[2]);
        String filename = identityParams[3];
        String filePath = G.DIR_TEMP + "/" + filename;
        int previousOffset = Integer.parseInt(identityParams[4]);
        if (identityParams[5].equals("true")) isFromHelperDownload = true;

        //
        //boolean avatarRequested = false;
        //long userId = -1;
        //RoomType roomType = RoomType.CHAT;
        //if (identityParams.length == 8) {
        //    avatarRequested = Boolean.parseBoolean(identityParams[5]);
        //    userId = Long.parseLong(identityParams[6]);
        //    roomType = RoomType.GROUP;
        //}

        long nextOffset = previousOffset + builder.getBytes().size();
        long progress = (nextOffset * 100) / fileSize;

        AndroidUtils.writeBytesToFile(filePath, builder.getBytes().toByteArray());

        //if (!avatarRequested) {
        //

        if (isFromHelperDownload) {
            if (G.onFileDownloadResponse != null) {
                G.onFileDownloadResponse.onFileDownload(token, nextOffset, selector, (int) progress);
            }
        } else {
            if (G.onFileDownloaded != null) {
                G.onFileDownloaded.onFileDownload(filename, token, fileSize, nextOffset, selector, (int) progress);
            }
        }

        //} else {
        //    G.onFileDownloadResponse.onAvatarDownload(token, nextOffset, selector, (int) progress, userId, roomType);
        //}
    }

    @Override public void timeOut() {
        super.timeOut();
    }

    @Override public void error() {
        super.error();

        ProtoError.ErrorResponse.Builder errorResponse = (ProtoError.ErrorResponse.Builder) message;
        int majorCode = errorResponse.getMajorCode();
        int minorCode = errorResponse.getMinorCode();

        if (isFromHelperDownload) {
            if (G.onFileDownloadResponse != null) {
                String[] identityParams = identity.split("\\*");
                String token = identityParams[0];

                ProtoFileDownload.FileDownload.Selector selector = ProtoFileDownload.FileDownload.Selector.valueOf(identityParams[1]);

                G.onFileDownloadResponse.onError(majorCode, minorCode, token, selector);
            }
        } else {
            if (G.onFileDownloaded != null) {
                G.onFileDownloaded.onError();
            }
        }
    }
}


