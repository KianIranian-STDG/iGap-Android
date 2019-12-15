package net.iGap.helper.downloadFile;

import net.iGap.fragments.emoji.OnStickerDownload;

public class IGDownloadFileStruct {

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
}