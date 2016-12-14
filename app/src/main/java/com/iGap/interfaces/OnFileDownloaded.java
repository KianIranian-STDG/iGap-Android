package com.iGap.interfaces;

import com.iGap.proto.ProtoFileDownload;

/**
 * just implement this interface in HelperAvatar
 */

public interface OnFileDownloaded {
    void onFileDownload(String fileName, String token, long offset, ProtoFileDownload.FileDownload.Selector selector, int progress);

    void onError();
}
