package net.iGap.helper.downloadFile;

import net.iGap.fragments.emoji.OnStickerDownload;

public class IGDownloadFileStruct {

    public IGDownloadFileStruct(String id, String token, long size, String path, OnDownloadListener onDownloadListener) {
        this.id = id;
        this.token = token;
        this.size = size;
        this.path = path;
        this.listener = onDownloadListener;
    }

    public IGDownloadFileStruct(String id, String token, long size, String path) {
        this.id = id;
        this.token = token;
        this.size = size;
        this.path = path;
    }

    public String id;
    public long offset;
    public String token;
    public long size;
    public String path;
    public long nextOffset;
    public long progress;
    public OnStickerDownload onStickerDownload;
    public OnDownloadListener listener;


    public interface OnDownloadListener {
        void onDownloadComplete(IGDownloadFileStruct fileStruct);

        void onDownloadFailed(IGDownloadFileStruct fileStruct);
    }
}