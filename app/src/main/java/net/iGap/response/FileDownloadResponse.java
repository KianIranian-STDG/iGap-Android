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
                if (HelperCheckInternetConnection.currentConnectivityType == HelperCheckInternetConnection.ConnectivityType.WIFI)
                    connectivityType = true;
                else
                    connectivityType = false;
            }
        } catch (Exception e) {
        }
        ;
        if (identityFileDownload.selector == ProtoFileDownload.FileDownload.Selector.FILE) {
            HelperDataUsage.progressDownload(connectivityType, builder.getBytes().size(), identityFileDownload.type);
        }
        long progress = (nextOffset * 100) / fileSize;

        if (progress == 100 && (identityFileDownload.selector == ProtoFileDownload.FileDownload.Selector.FILE)) {
            HelperDataUsage.insertDataUsage(HelperDataUsage.convetredDownloadType, connectivityType, true);
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
                    G.onStickerDownloaded.onStickerDownloaded(filePath, cacheId, fileSize, nextOffset, identityFileDownload.selector ,identityFileDownload.typeDownload , (int) 0);
                }
                break;
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
        RequestFileDownload.IdentityFileDownload identityFileDownload = ((RequestFileDownload.IdentityFileDownload) identity);
        type = identityFileDownload.typeDownload;

        if (type == RequestFileDownload.TypeDownload.FILE) {
            if (G.onFileDownloadResponse != null) {
                G.onFileDownloadResponse.onError(majorCode, minorCode, identityFileDownload.cacheId, identityFileDownload.selector);
            }
        } else if (type == RequestFileDownload.TypeDownload.AVATAR) {
            if (G.onFileDownloaded != null) {
                G.onFileDownloaded.onError(majorCode, identity);
            }
        } else if (type == RequestFileDownload.TypeDownload.STICKER|| type == RequestFileDownload.TypeDownload.STICKER_DETAIL) {
            if (G.onStickerDownloaded != null) {
                G.onStickerDownloaded.onError(majorCode, identity);
            }

        }
    }
}


