package net.iGap.fragments.emoji;

import net.iGap.helper.downloadFile.IGDownloadFileStruct;

public interface OnStickerDownload {
    void onStickerDownload(IGDownloadFileStruct igDownloadFileStruct);

    void onError(IGDownloadFileStruct igDownloadFileStruct, int major, int minor);
}
