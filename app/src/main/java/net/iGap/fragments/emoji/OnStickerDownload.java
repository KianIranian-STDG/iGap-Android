package net.iGap.fragments.emoji;

public interface OnStickerDownload {
    void onStickerDownload(IGDownloadFileStruct igDownloadFileStruct);

    void onError(IGDownloadFileStruct igDownloadFileStruct, int major, int minor);
}
