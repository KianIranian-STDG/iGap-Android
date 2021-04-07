/*
 * This is the source code of iGap for Android
 * It is licensed under GNU AGPL v3.0
 * You should have received a copy of the license in this archive (see LICENSE).
 * Copyright © 2017 , iGap - www.iGap.net
 * iGap Messenger | Free, Fast and Secure instant messaging application
 * The idea of the Kianiranian Company - www.kianiranian.com
 * All rights reserved.
 */

package net.iGap.response;

import net.iGap.G;
import net.iGap.helper.HelperCheckInternetConnection;
import net.iGap.helper.HelperDataUsage;
import net.iGap.helper.HelperLog;
import net.iGap.helper.downloadFile.IGDownloadFileStruct;
import net.iGap.module.AndroidUtils;
import net.iGap.proto.ProtoError;
import net.iGap.proto.ProtoFileDownload;
import net.iGap.request.RequestFileDownload;

public class FileDownloadResponse extends MessageHandler {

    public int actionId;
    public Object message;
    public Object identity;
    public RequestFileDownload.TypeDownload type = RequestFileDownload.TypeDownload.FILE;
    private long nextOffset;

    public FileDownloadResponse(int actionId, Object protoClass, Object identity) {
        super(actionId, protoClass, identity);

        this.message = protoClass;
        this.actionId = actionId;
        this.identity = identity;
    }

    @Override
    public void handler() {
        super.handler();
        ProtoFileDownload.FileDownloadResponse.Builder builder = (ProtoFileDownload.FileDownloadResponse.Builder) message;

        if (identity instanceof IGDownloadFileStruct) {
            IGDownloadFileStruct fileStruct = (IGDownloadFileStruct) identity;

            fileStruct.nextOffset = fileStruct.offset + builder.getBytes().toByteArray().length;

            fileStruct.progress = (fileStruct.nextOffset * 100) / fileStruct.size;

            AndroidUtils.writeBytesToFile(fileStruct.path, builder.getBytes().toByteArray());

            if (fileStruct.onStickerDownload != null)
                fileStruct.onStickerDownload.onStickerDownload(fileStruct);

        } else {
            RequestFileDownload.IdentityFileDownload identityFileDownload = ((RequestFileDownload.IdentityFileDownload) identity);
            String cacheId = identityFileDownload.cacheId;
            long fileSize = identityFileDownload.size;
            String filePath = identityFileDownload.filepath;
            int previousOffset = (int) identityFileDownload.offset;
            type = identityFileDownload.typeDownload;

            //  String type = filePath.substring(filePath.lastIndexOf(".") + 1);
            nextOffset = previousOffset + builder.getBytes().size();


            boolean connectivityType = true;
            try {
                if (HelperCheckInternetConnection.currentConnectivityType != null) {
                    connectivityType = HelperCheckInternetConnection.currentConnectivityType == HelperCheckInternetConnection.ConnectivityType.WIFI;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (identityFileDownload.selector == ProtoFileDownload.FileDownload.Selector.FILE) {
                HelperDataUsage.progressDownload(builder.getBytes().size(), identityFileDownload.type);
            }
            long progress = (nextOffset * 100) / fileSize;

            RequestFileDownload.downloadPending.remove(cacheId + "" + identityFileDownload.offset);

            if (progress == 100 && (identityFileDownload.selector == ProtoFileDownload.FileDownload.Selector.FILE)) {
                HelperDataUsage.increaseDownloadFiles(identityFileDownload.type);
            }

            AndroidUtils.writeBytesToFile(filePath, builder.getBytes().toByteArray());

            switch (type) {
                case FILE:
                    if (G.onFileDownloadResponse != null) {
                        G.onFileDownloadResponse.onFileDownload(cacheId, nextOffset, identityFileDownload.selector, (int) progress);
                    }
                    break;
                case AVATAR:
                    if (G.onFileDownloaded != null) {
                        G.onFileDownloaded.onFileDownload(filePath, cacheId, fileSize, nextOffset, identityFileDownload.selector, (int) progress);
                    }
                    break;
                case STICKER:
                case STICKER_DETAIL:
                    if (G.onStickerDownloaded != null) {
                        G.onStickerDownloaded.onStickerDownloaded(filePath, cacheId, fileSize, nextOffset, identityFileDownload.selector, identityFileDownload.typeDownload, 0);
                    }
                    break;
            }
        }
    }

    @Override
    public void timeOut() {
        super.timeOut();
    }

    @Override
    public void error() {
        super.error();

        ProtoError.ErrorResponse.Builder errorResponse = (ProtoError.ErrorResponse.Builder) message;
        int majorCode = errorResponse.getMajorCode();
        int minorCode = errorResponse.getMinorCode();

        if (identity instanceof IGDownloadFileStruct) {
            IGDownloadFileStruct fileStruct = (IGDownloadFileStruct) identity;

            if (fileStruct.onStickerDownload != null)
                fileStruct.onStickerDownload.onError(fileStruct, majorCode, minorCode);
        } else {

        RequestFileDownload.IdentityFileDownload identityFileDownload = ((RequestFileDownload.IdentityFileDownload) identity);
        if (majorCode == 713 && (minorCode == 2 || minorCode == 5)) {
            HelperLog.getInstance().setErrorLog(new Exception("error: " + identityFileDownload.cacheId
                    + " offset: " + identityFileDownload.offset
                    + " size: " + identityFileDownload.size
                    + " typeDownload: " + identityFileDownload.typeDownload
                    + " majorCode: " + majorCode
                    + " minorCode: " + minorCode));
        }
        type = identityFileDownload.typeDownload;
        RequestFileDownload.downloadPending.remove(identityFileDownload.cacheId + "" + identityFileDownload.offset);

            if (type == RequestFileDownload.TypeDownload.FILE) {
                if (G.onFileDownloadResponse != null) {
                    G.onFileDownloadResponse.onError(majorCode, minorCode, identityFileDownload.cacheId, identityFileDownload.selector);
                }
            } else if (type == RequestFileDownload.TypeDownload.AVATAR) {
                if (G.onFileDownloaded != null) {
                    G.onFileDownloaded.onError(majorCode, identity);
                }
            } else if (type == RequestFileDownload.TypeDownload.STICKER || type == RequestFileDownload.TypeDownload.STICKER_DETAIL) {
                if (G.onStickerDownloaded != null) {
                    G.onStickerDownloaded.onError(majorCode, identity);
                }

            }
        }
    }
}


